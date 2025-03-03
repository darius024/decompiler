.intel_syntax noprefix
.globl main
.section .rodata
# length of .L.str0
	.int 4
.L.str0:
	.asciz " = {"
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
	# push {rbx, r12, r13}
	sub rsp, 24
	mov qword ptr [rsp], rbx
	mov qword ptr [rsp + 8], r12
	mov qword ptr [rsp + 16], r13
	mov rbp, rsp
	# 10 element array
	mov edi, 44
	call _malloc
	mov r11, rax
	# array pointers are shifted forwards by 4 bytes (to account for size)
	mov r11, r11
	add r11, 4
	mov eax, 10
	mov dword ptr [r11 - 4], eax
	mov eax, 0
	mov dword ptr [r11], eax
	mov eax, 0
	mov dword ptr [r11 + 4], eax
	mov eax, 0
	mov dword ptr [r11 + 8], eax
	mov eax, 0
	mov dword ptr [r11 + 12], eax
	mov eax, 0
	mov dword ptr [r11 + 16], eax
	mov eax, 0
	mov dword ptr [r11 + 20], eax
	mov eax, 0
	mov dword ptr [r11 + 24], eax
	mov eax, 0
	mov dword ptr [r11 + 28], eax
	mov eax, 0
	mov dword ptr [r11 + 32], eax
	mov eax, 0
	mov dword ptr [r11 + 36], eax
	mov rax, r11
	mov r12, rax
	mov eax, 0
	mov r13d, eax
	jmp .L0
.L1:
	mov r10d, r13d
	mov eax, r13d
	mov r9, r12
	call _arrStore4
	mov eax, r13d
	add eax, 1
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r13d, eax
.L0:
	cmp r13d, 10
	jl .L1
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printp
	lea rax, [rip + .L.str0]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	mov eax, 0
	mov r13d, eax
	jmp .L2
.L3:
	mov r10d, r13d
	mov r9, r12
	call _arrLoad4
	mov eax, r9d
	mov eax, eax
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	cmp r13d, 9
	jl .L4
	jmp .L5
.L4:
	lea rax, [rip + .L.str1]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
.L5:
	mov eax, r13d
	add eax, 1
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r13d, eax
.L2:
	cmp r13d, 10
	jl .L3
	lea rax, [rip + .L.str2]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	mov rax, 0
	# pop/peek {rbx, r12, r13}
	mov rbx, qword ptr [rsp]
	mov r12, qword ptr [rsp + 8]
	mov r13, qword ptr [rsp + 16]
	add rsp, 24
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

_arrLoad4:
	# Special calling convention: array ptr passed in R9, index in R10, and return into R9
	push rbx
	# `test r, r` is equivalent to `cmp r, 0`
	test r10d, r10d
	cmovl rsi, r10 # this must be a 64-bit move so that it doesn't truncate if the move fails
	jl _errOutOfBounds
	mov ebx, dword ptr [r9 - 4]
	cmp r10d, ebx
	cmovge rsi, r10 # this must be a 64-bit move so that it doesn't truncate if the move fails
	jge _errOutOfBounds
	mov r9d, dword ptr [r9 + 4*r10]
	pop rbx
	ret

_arrStore4:
	# Special calling convention: array ptr passed in R9, index in R10, value to store in RAX
	push rbx
	# `test r, r` is equivalent to `cmp r, 0`
	test r10d, r10d
	cmovl rsi, r10 # this must be a 64-bit move so that it doesn't truncate if the move fails
	jl _errOutOfBounds
	mov ebx, dword ptr [r9 - 4]
	cmp r10d, ebx
	cmovge rsi, r10 # this must be a 64-bit move so that it doesn't truncate if the move fails
	jge _errOutOfBounds
	mov dword ptr [r9 + 4*r10], eax
	pop rbx
	ret

.section .rodata
# length of .L._errOutOfBounds_str0
	.int 42
.L._errOutOfBounds_str0:
	.asciz "fatal error: array index %d out of bounds\n"
.text
_errOutOfBounds:
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	lea rdi, [rip + .L._errOutOfBounds_str0]
	# on x86, al represents the number of SIMD registers used as variadic arguments
	mov al, 0
	call printf@plt
	mov rdi, 0
	call fflush@plt
	mov dil, -1
	call exit@plt

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
