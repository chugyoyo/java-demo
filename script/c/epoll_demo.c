#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/epoll.h>
#include <errno.h>

#define MAX_EVENTS 1024
#define BUFFER_SIZE 1024
#define PORT 8080

// 设置socket为非阻塞模式
void set_nonblocking(int sockfd) {
    int flags = fcntl(sockfd, F_GETFL, 0);
    fcntl(sockfd, F_SETFL, flags | O_NONBLOCK);
}

int main() {
    int server_fd, epoll_fd;
    struct sockaddr_in server_addr;
    struct epoll_event ev, events[MAX_EVENTS];

    // 1. 创建服务器socket
    server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd == -1) {
        perror("socket creation failed");
        exit(EXIT_FAILURE);
    }

    // 设置SO_REUSEADDR避免"Address already in use"
    int opt = 1;
    setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

    // 2. 绑定地址和端口
    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(PORT);

    if (bind(server_fd, (struct sockaddr*)&server_addr, sizeof(server_addr)) == -1) {
        perror("bind failed");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    // 3. 开始监听
    if (listen(server_fd, SOMAXCONN) == -1) {
        perror("listen failed");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    printf("Server listening on port %d...\n", PORT);

    // 4. 创建epoll实例
    epoll_fd = epoll_create1(0);
    if (epoll_fd == -1) {
        perror("epoll_create1 failed");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    // 5. 将服务器socket添加到epoll监听
    ev.events = EPOLLIN;  // 监听可读事件
    ev.data.fd = server_fd;
    if (epoll_ctl(epoll_fd, EPOLL_CTL_ADD, server_fd, &ev) == -1) {
        perror("epoll_ctl: server_fd");
        close(server_fd);
        close(epoll_fd);
        exit(EXIT_FAILURE);
    }

    // 6. 事件循环
    while (1) {
        int nfds = epoll_wait(epoll_fd, events, MAX_EVENTS, -1); // -1表示无限等待
        if (nfds == -1) {
            perror("epoll_wait");
            break;
        }

        printf("epoll_wait returned %d events\n", nfds);

        for (int i = 0; i < nfds; i++) {
            // 6.1 新客户端连接
            if (events[i].data.fd == server_fd) {
                struct sockaddr_in client_addr;
                socklen_t client_len = sizeof(client_addr);
                int client_fd = accept(server_fd, (struct sockaddr*)&client_addr, &client_len);

                if (client_fd == -1) {
                    perror("accept");
                    continue;
                }

                // 设置客户端socket为非阻塞
                set_nonblocking(client_fd);

                // 将新客户端添加到epoll监听
                ev.events = EPOLLIN | EPOLLET;  // 边缘触发模式
                ev.data.fd = client_fd;
                if (epoll_ctl(epoll_fd, EPOLL_CTL_ADD, client_fd, &ev) == -1) {
                    perror("epoll_ctl: client_fd");
                    close(client_fd);
                } else {
                    printf("New client connected: %s:%d (fd: %d)\n",
                           inet_ntoa(client_addr.sin_addr),
                           ntohs(client_addr.sin_port),
                           client_fd);
                }
            }
            // 6.2 客户端数据可读
            else {
                int client_fd = events[i].data.fd;
                char buffer[BUFFER_SIZE];

                // 读取数据
                ssize_t count = read(client_fd, buffer, BUFFER_SIZE - 1);

                if (count > 0) {
                    buffer[count] = '\0';
                    printf("Received from client %d: %s", client_fd, buffer);

                    // 回显数据给客户端
                    write(client_fd, buffer, count);
                }
                else if (count == 0 || (count == -1 && errno != EAGAIN)) {
                    // 客户端断开连接或读取错误
                    printf("Client %d disconnected\n", client_fd);
                    epoll_ctl(epoll_fd, EPOLL_CTL_DEL, client_fd, NULL);
                    close(client_fd);
                }
                // 如果是EAGAIN错误，在边缘触发模式下表示数据已读完，继续等待下次事件
            }
        }
    }

    // 清理资源
    close(server_fd);
    close(epoll_fd);
    return 0;
}