#dd：Linux 下的拷贝工具
#
#if=/dev/zero：输入文件是 /dev/zero，它会不断输出字节 0x00，适合快速造大文件
#
#of=/tmp/testfile.bin：输出文件路径
#
#bs=1M：每次写 1MB 块大小
#
#count=1024：写 1024 个块 → 1MB × 1024 = 1GB
dd if=/dev/zero of=/tmp/testfile.bin bs=1M count=1024