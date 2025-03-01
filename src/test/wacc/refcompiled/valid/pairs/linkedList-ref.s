.intel_syntax noprefix
.globl main
.section .rodata
# length of .L.str0
	.int 8
.L.str0:
	.asciz "list = {"
# length of .L.str1
	.int 2
.L.str1:
	.asciz ", "
# length of .L.str2
	.int 1
.L.str2:
	.asciz "}"
.text
main:
	push rbp
	# push {rbx, r12, r13, r14, r15}
	sub rsp, 40
	mov qword ptr [rsp], rbx
	mov qword ptr [rsp + 8], r12
	mov qword ptr [rsp + 16], r13
	mov qword ptr [rsp + 24], r14
	mov qword ptr [rsp + 32], r15
	mov rbp, rsp
	# push {rcx, rdx, rsi}
	sub rsp, 24
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov edi, 16
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	add rsp, 24
	mov eax, 11
	mov qword ptr [r11], rax
	mov rax, 0
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r12, rax
	# push {rcx, rdx, rsi}
	sub rsp, 24
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov edi, 16
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	add rsp, 24
	mov eax, 4
	mov qword ptr [r11], rax
	mov rax, r12
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r13, rax
	# push {rcx, rdx, rsi}
	sub rsp, 24
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov edi, 16
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	add rsp, 24
	mov eax, 2
	mov qword ptr [r11], rax
	mov rax, r13
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r14, rax
	# push {rcx, rdx, rsi}
	sub rsp, 24
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov edi, 16
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	add rsp, 24
	mov eax, 1
	mov qword ptr [r11], rax
	mov rax, r14
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r15, rax
	# push {rcx, rdx, rsi}
	sub rsp, 24
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str0]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	add rsp, 24
	mov rax, r15
	mov rcx, rax
	cmp rcx, 0
	je _errNull
	mov rax, qword ptr [rcx + 8]
	mov rax, rax
	mov rdx, rax
	mov eax, 0
	mov esi, eax
	jmp .L0
.L1:
	cmp rcx, 0
	je _errNull
	mov rax, qword ptr [rcx]
	mov rax, rax
	push rax
	pop rax
	mov eax, eax
	mov esi, eax
	# push {rcx, rdx, rsi}
	sub rsp, 24
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, esi
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	# pop/peek {rcx, rdx, rsi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	add rsp, 24
	# push {rcx, rdx, rsi}
	sub rsp, 24
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str1]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	add rsp, 24
	mov rax, rdx
	mov rcx, rax
	cmp rcx, 0
	je _errNull
	mov rax, qword ptr [rcx + 8]
	mov rax, rax
	push rax
	pop rax
	mov rax, rax
	mov rdx, rax
.L0:
	cmp rdx, 0
	jne .L1
	cmp rcx, 0
	je _errNull
	mov rax, qword ptr [rcx]
	mov rax, rax
	push rax
	pop rax
	mov eax, eax
	mov esi, eax
	# push {rcx, rdx, rsi}
	sub rsp, 24
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, esi
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	# pop/peek {rcx, rdx, rsi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	add rsp, 24
	# push {rcx, rdx, rsi}
	sub rsp, 24
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str2]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	# pop/peek {rcx, rdx, rsi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	add rsp, 24
	mov rax, 0
	# pop/peek {rbx, r12, r13, r14, r15}
	mov rbx, qword ptr [rsp]
	mov r12, qword ptr [rsp + 8]
	mov r13, qword ptr [rsp + 16]
	mov r14, qword ptr [rsp + 24]
	mov r15, qword ptr [rsp + 32]
	add rsp, 40
	pop rbp
	ret

.section .rodata
# length of .L._printi_str0
	.int 2
.L._printi_str0:
	.asciz "%d"
.text
_printi:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	mov esi, edi
	lea rdi, [rip + .L._printi_str0]
	# on x86, al represents the number of SIMD registers used as variadic arguments
	mov al, 0
	call printf@plt
	mov rdi, 0
	call fflush@plt
	mov rsp, rbp
	pop rbp
	ret

.section .rodata
# length of .L._prints_str0
	.int 4
.L._prints_str0:
	.asciz "%.*s"
.text
_prints:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	mov rdx, rdi
	mov esi, dword ptr [rdi - 4]
	lea rdi, [rip + .L._prints_str0]
	# on x86, al represents the number of SIMD registers used as variadic arguments
	mov al, 0
	call printf@plt
	mov rdi, 0
	call fflush@plt
	mov rsp, rbp
	pop rbp
	ret

.section .rodata
# length of .L._println_str0
	.int 0
.L._println_str0:
	.asciz ""
.text
_println:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	lea rdi, [rip + .L._println_str0]
	call puts@plt
	mov rdi, 0
	call fflush@plt
	mov rsp, rbp
	pop rbp
	ret

_malloc:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	call malloc@plt
	cmp rax, 0
	je _errOutOfMemory
	mov rsp, rbp
	pop rbp
	ret

.section .rodata
# length of .L._errNull_str0
	.int 45
.L._errNull_str0:
	.asciz "fatal error: null pair dereferenced or freed\n"
.text
_errNull:
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	lea rdi, [rip + .L._errNull_str0]
	call _prints
	mov dil, -1
	call exit@plt

.section .rodata
# length of .L._errOutOfMemory_str0
	.int 27
.L._errOutOfMemory_str0:
	.asciz "fatal error: out of memory\n"
.text
_errOutOfMemory:
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	lea rdi, [rip + .L._errOutOfMemory_str0]
	call _prints
	mov dil, -1
	call exit@plt
