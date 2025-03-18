main:
        push    rbp
        mov     rbp, rsp
        mov     dword ptr [rbp-8], 0
        mov     dword ptr [rbp-4], 4
        mov     ebx, dword ptr [rbp-8]
        cmp     ebx, 1
        jne     .L2
        mov     dword ptr [rbp-4], 4
        jmp     .L3
.L2:
        mov     dword ptr [rbp-4], 6
.L3:
        mov     eax, dword ptr [rbp-4]
        mov     dword ptr [rbp-12], eax
        mov     eax, 0
        pop     rbp
        ret
