.intel_syntax noprefix
.globl main
.section .rodata
# length of .L.str0
	.int 5
.L.str0:
	.asciz "a is "
# length of .L.str1
	.int 5
.L.str1:
	.asciz "b is "
# length of .L.str2
	.int 5
.L.str2:
	.asciz "c is "
# length of .L.str3
	.int 5
.L.str3:
	.asciz "d is "
# length of .L.str4
	.int 5
.L.str4:
	.asciz "e is "
# length of .L.str5
	.int 5
.L.str5:
	.asciz "f is "
# length of .L.str6
	.int 5
.L.str6:
	.asciz "hello"
# length of .L.str7
	.int 10
.L.str7:
	.asciz "answer is "
.text
main:
	push rbp
	# push {rbx, r12, r13, r14}
	sub rsp, 32
	mov qword ptr [rsp], rbx
	mov qword ptr [rsp + 8], r12
	mov qword ptr [rsp + 16], r13
	mov qword ptr [rsp + 24], r14
	mov rbp, rsp
	# 2 element array
	mov edi, 6
	call _malloc
	mov r11, rax
	# array pointers are shifted forwards by 4 bytes (to account for size)
	mov r11, r11
	add r11, 4
	mov eax, 2
	mov dword ptr [r11 - 4], eax
	mov al, 0
	mov byte ptr [r11], al
	mov al, 1
	mov byte ptr [r11 + 1], al
	mov rax, r11
	mov r12, rax
	# 2 element array
	mov edi, 12
	call _malloc
	mov r11, rax
	# array pointers are shifted forwards by 4 bytes (to account for size)
	mov r11, r11
	add r11, 4
	mov eax, 2
	mov dword ptr [r11 - 4], eax
	mov eax, 1
	mov dword ptr [r11], eax
	mov eax, 2
	mov dword ptr [r11 + 4], eax
	mov rax, r11
	mov r13, rax
	mov eax, 42
	mov edi, eax
	mov al, 1
	mov sil, al
	mov al, 117
	mov dl, al
	lea rax, [rip + .L.str6]
	push rax
	pop rax
	mov rax, rax
	mov rcx, rax
	mov rax, r12
	mov r8, rax
	mov rax, r13
	mov r9, rax
	call wacc_doSomething
	mov r11b, al
	mov al, r11b
	mov r14b, al
	lea rax, [rip + .L.str7]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	mov al, r14b
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	call _println
	mov rax, 0
	# pop/peek {rbx, r12, r13, r14}
	mov rbx, qword ptr [rsp]
	mov r12, qword ptr [rsp + 8]
	mov r13, qword ptr [rsp + 16]
	mov r14, qword ptr [rsp + 24]
	add rsp, 32
	pop rbp
	ret

wacc_doSomething:
	push rbp
	mov rbp, rsp
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str0]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, edi
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str1]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, sil
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printb
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str2]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, dl
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str3]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rcx
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str4]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r8
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printp
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str5]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r9
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printp
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, 103
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

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
# length of .L._printc_str0
	.int 2
.L._printc_str0:
	.asciz "%c"
.text
_printc:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	mov sil, dil
	lea rdi, [rip + .L._printc_str0]
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
# length of .L._printb_str0
	.int 5
.L._printb_str0:
	.asciz "false"
# length of .L._printb_str1
	.int 4
.L._printb_str1:
	.asciz "true"
# length of .L._printb_str2
	.int 4
.L._printb_str2:
	.asciz "%.*s"
.text
_printb:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	cmp dil, 0
	jne .L_printb0
	lea rdx, [rip + .L._printb_str0]
	jmp .L_printb1
.L_printb0:
	lea rdx, [rip + .L._printb_str1]
.L_printb1:
	mov esi, dword ptr [rdx - 4]
	lea rdi, [rip + .L._printb_str2]
	# on x86, al represents the number of SIMD registers used as variadic arguments
	mov al, 0
	call printf@plt
	mov rdi, 0
	call fflush@plt
	mov rsp, rbp
	pop rbp
	ret

.section .rodata
# length of .L._printp_str0
	.int 2
.L._printp_str0:
	.asciz "%p"
.text
_printp:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	mov rsi, rdi
	lea rdi, [rip + .L._printp_str0]
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
