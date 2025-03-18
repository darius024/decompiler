main:
        push    rbp
        mov     rbp, rsp
        mov     dword ptr [rbp-4], 1
        mov     dword ptr [rbp-8], 2
        mov     edx, dword ptr [rbp-4]
        mov     eax, dword ptr [rbp-8]
        add     eax, edx
        mov     dword ptr [rbp-12], eax
        mov     eax, dword ptr [rbp-12]
        mov     ebx, dword ptr [rbp-8]
        sub     eax, ebx
        mov     dword ptr [rbp-16], eax
        mov     eax, 0
        pop     rbp
        ret
