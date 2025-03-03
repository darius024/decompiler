.intel_syntax noprefix
.globl main
.section .rodata
# length of .L.str0
	.int 12
.L.str0:
	.asciz "r1: sending "
# length of .L.str1
	.int 13
.L.str1:
	.asciz "r2: received "
.text
main:
	push rbp
	# push {rbx, r12}
	sub rsp, 16
	mov qword ptr [rsp], rbx
	mov qword ptr [rsp + 8], r12
	mov rbp, rsp
	mov eax, 0
	mov r12d, eax
	mov eax, 8
	mov edi, eax
	call wacc_r1
	mov r11d, eax
	mov eax, r11d
	mov r12d, eax
	mov rax, 0
	# pop/peek {rbx, r12}
	mov rbx, qword ptr [rsp]
	mov r12, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret

wacc_r1:
	push rbp
	push r12
	mov rbp, rsp
	cmp edi, 0
	je .L0
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str0]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	pop rdi
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, edi
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	pop rdi
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, edi
	mov edi, eax
	call wacc_r2
	mov r11d, eax
	pop rdi
	mov eax, r11d
	mov r12d, eax
	jmp .L1
.L0:
.L1:
	mov eax, 42
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_r2:
	push rbp
	push r12
	mov rbp, rsp
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str1]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	pop rdi
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, edi
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	pop rdi
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, edi
	sub eax, 1
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov edi, eax
	call wacc_r1
	mov r11d, eax
	pop rdi
	mov eax, r11d
	mov r12d, eax
	mov eax, 44
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
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

.section .rodata
# length of .L._errOverflow_str0
	.int 52
.L._errOverflow_str0:
	.asciz "fatal error: integer overflow or underflow occurred\n"
.text
_errOverflow:
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	lea rdi, [rip + .L._errOverflow_str0]
	call _prints
	mov dil, -1
	call exit@plt
