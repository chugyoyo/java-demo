const {createApp, onMounted, ref, reactive, nextTick} = Vue

createApp({
    setup() {
        const chatEl = ref(null)
        const ta = ref(null)
        const aborter = ref(null)

        const state = reactive({
            baseUrl: localStorage.getItem('ollama_base') || 'http://localhost:11434',
            model: localStorage.getItem('ollama_model') || 'llama3.2',
            stream: true,
        })

        const messages = ref([
            {role: 'ai', content: '你好！我是本地模型助手，直接输入即可开始对话。', meta: {model: state.model}}
        ])
        const draft = ref('')
        const isLoading = ref(false)
        const connected = ref(false)
        const error = ref('')

        function savePrefs() {
            localStorage.setItem('ollama_base', state.baseUrl)
            localStorage.setItem('ollama_model', state.model)
        }

        function scrollToBottom() {
            nextTick(() => chatEl.value?.scrollTo({top: chatEl.value.scrollHeight, behavior: 'smooth'}))
        }

        function newline(e) { /* just allow */
        }

        async function testConnection() {
            error.value = ''
            try {
                const r = await fetch(state.baseUrl + '/api/tags', {method: 'GET'})
                connected.value = r.ok
                if (!r.ok) throw new Error('无法访问 ' + state.baseUrl + '，请确认 Ollama 已启动并允许 CORS')
            } catch (e) {
                connected.value = false
                error.value = e.message
            }
        }

        async function send() {
            if (!draft.value.trim() || isLoading.value) return
            error.value = ''
            savePrefs()

            const userMsg = {role: 'user', content: draft.value}
            messages.value.push(userMsg)
            draft.value = ''
            await nextTick()
            scrollToBottom()

            const aiMsg = {role: 'ai', content: '', meta: {model: state.model}}
            messages.value.push(aiMsg)

            try {
                isLoading.value = true
                const controller = new AbortController()
                aborter.value = controller

                const body = {
                    model: state.model,
                    messages: messages.value
                        .filter(m => m.role === 'user' || m.role === 'ai')
                        .map(m => ({
                            role: m.role,
                            content: m.content
                        })),
                    stream: state.stream,
                }

                const resp = await fetch(state.baseUrl + '/api/chat', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(body),
                    signal: controller.signal,
                })

                if (!resp.ok) throw new Error('请求失败：' + resp.status + ' ' + resp.statusText)

                if (state.stream) {
                    const reader = resp.body.getReader()
                    const decoder = new TextDecoder('utf-8')
                    let buf = ''
                    const started = performance.now()

                    while (true) {
                        const {value, done} = await reader.read()
                        if (done) break
                        buf += decoder.decode(value, {stream: true})

                        let lines = buf.split('\n')
                        buf = lines.pop()
                        for (const line of lines) {
                            if (!line.trim()) continue
                            try {
                                const j = JSON.parse(line)
                                if (j.message && typeof j.message.content === 'string') {
                                    aiMsg.content += j.message.content
                                    scrollToBottom()
                                }
                                if (j.done) {
                                    const secs = (performance.now() - started) / 1000
                                    if (j.total_duration && j.eval_count) {
                                        const tps = (j.eval_count / (j.total_duration / 1e9)).toFixed(2)
                                        aiMsg.meta.tps = tps
                                    }
                                    aiMsg.meta.time = new Date().toLocaleTimeString()
                                }
                            } catch (e) {
                            }
                        }
                    }
                    scrollToBottom()
                } else {
                    const j = await resp.json()
                    aiMsg.content = j.message?.content || j.response || '[无内容]'
                    if (j.total_duration && j.eval_count) {
                        aiMsg.meta.tps = (j.eval_count / (j.total_duration / 1e9)).toFixed(2)
                    }
                    aiMsg.meta.time = new Date().toLocaleTimeString()
                }
            } catch (e) {
                error.value = e.message || String(e)
                aiMsg.content += '\n\n[出错] ' + error.value
            } finally {
                isLoading.value = false
                aborter.value = null
                scrollToBottom()
            }
        }

        function abort() {
            try {
                aborter.value?.abort()
            } catch {
            }
        }

        function clearChat() {
            messages.value = []
        }

        onMounted(() => {
            testConnection()
        })

        return {
            state,
            messages,
            draft,
            isLoading,
            error,
            connected,
            chatEl,
            ta,
            send,
            newline,
            abort,
            clearChat,
            testConnection
        }
    }
}).mount('#app')