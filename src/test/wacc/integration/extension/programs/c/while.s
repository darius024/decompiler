main:
        push    rbp
        mov     rbp, rsp
        mov     dword ptr [rbp-4], 0
        mov     dword ptr [rbp-8], 2
        jmp     .L2
.L3:
        mov     ebx, dword ptr [rbp-4]
        add     ebx, 1
        mov     ebx, dword ptr [rbp-8]
        add     ebx, 10
.L2:
        mov     ebx, dword ptr [rbp-4]
        cmp     ebx, 9
        jle     .L3
        mov     eax, 0
        pop     rbp
        ret
