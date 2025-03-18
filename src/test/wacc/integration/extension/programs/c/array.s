main:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     edi, 8
        call    malloc
        mov     qword ptr [rbp-8], rax
        mov     rax, qword ptr [rbp-8]
        mov     dword ptr [rax], 1
        mov     rax, qword ptr [rbp-8]
        add     rax, 4
        mov     dword ptr [rax], 2
        mov     rax, qword ptr [rbp-8]
        mov     qword ptr [rbp-16], rax
        mov     eax, 0
        ret
