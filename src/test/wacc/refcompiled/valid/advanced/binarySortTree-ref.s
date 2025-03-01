.intel_syntax noprefix
.globl main
.section .rodata
# length of .L.str0
	.int 47
.L.str0:
	.asciz "Please enter the number of integers to insert: "
# length of .L.str1
	.int 10
.L.str1:
	.asciz "There are "
# length of .L.str2
	.int 10
.L.str2:
	.asciz " integers."
# length of .L.str3
	.int 36
.L.str3:
	.asciz "Please enter the number at position "
# length of .L.str4
	.int 3
.L.str4:
	.asciz " : "
# length of .L.str5
	.int 29
.L.str5:
	.asciz "Here are the numbers sorted: "
# length of .L.str6
	.int 0
.L.str6:
	.asciz ""
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
	mov eax, 0
	mov r12d, eax
	lea rax, [rip + .L.str0]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# load the current value in the destination of the read so it supports defaults
	mov eax, r12d
	mov edi, eax
	call _readi
	mov r11d, eax
	mov eax, r11d
	mov r12d, eax
	lea rax, [rip + .L.str1]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	mov eax, r12d
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	lea rax, [rip + .L.str2]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	mov eax, 0
	mov r13d, eax
	mov rax, 0
	mov r14, rax
	jmp .L6
.L7:
	mov eax, 0
	mov r15d, eax
	lea rax, [rip + .L.str3]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	mov eax, r13d
	add eax, 1
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	lea rax, [rip + .L.str4]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# load the current value in the destination of the read so it supports defaults
	mov eax, r15d
	mov edi, eax
	call _readi
	mov r11d, eax
	mov eax, r11d
	mov r15d, eax
	mov rax, r14
	mov rdi, rax
	mov eax, r15d
	mov esi, eax
	call wacc_insert
	mov r11, rax
	mov rax, r11
	mov r14, rax
	mov eax, r13d
	add eax, 1
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r13d, eax
.L6:
	cmp r13d, r12d
	jl .L7
	lea rax, [rip + .L.str5]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	mov rax, r14
	mov rdi, rax
	call wacc_printTree
	mov r11d, eax
	mov eax, r11d
	mov r13d, eax
	lea rax, [rip + .L.str6]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
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

wacc_createNewNode:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	# push {rdx, rsi, rdi}
	sub rsp, 24
	mov qword ptr [rsp], rdx
	mov qword ptr [rsp + 8], rsi
	mov qword ptr [rsp + 16], rdi
	mov edi, 16
	call _malloc
	mov r11, rax
	# pop/peek {rdx, rsi, rdi}
	mov rdx, qword ptr [rsp]
	mov rsi, qword ptr [rsp + 8]
	mov rdi, qword ptr [rsp + 16]
	add rsp, 24
	mov rax, rsi
	mov qword ptr [r11], rax
	mov rax, rdx
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r12, rax
	# push {rdx, rsi, rdi}
	sub rsp, 24
	mov qword ptr [rsp], rdx
	mov qword ptr [rsp + 8], rsi
	mov qword ptr [rsp + 16], rdi
	mov edi, 16
	call _malloc
	mov r11, rax
	# pop/peek {rdx, rsi, rdi}
	mov rdx, qword ptr [rsp]
	mov rsi, qword ptr [rsp + 8]
	mov rdi, qword ptr [rsp + 16]
	add rsp, 24
	mov eax, edi
	mov qword ptr [r11], rax
	mov rax, r12
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r13, rax
	mov rax, r13
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_insert:
	push rbp
	# push {r12, r13, r14}
	sub rsp, 24
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov rbp, rsp
	cmp rdi, 0
	je .L0
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r12, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov eax, eax
	mov r13d, eax
	mov rax, 0
	mov r14, rax
	cmp esi, r13d
	jl .L2
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	push rax
	pop rax
	mov rax, rax
	mov r14, rax
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	call wacc_insert
	mov r11, rax
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	cmp r12, 0
	je _errNull
	mov rax, r11
	mov qword ptr [r12 + 8], rax
	jmp .L3
.L2:
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	push rax
	pop rax
	mov rax, rax
	mov r14, rax
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	call wacc_insert
	mov r11, rax
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	cmp r12, 0
	je _errNull
	mov rax, r11
	mov qword ptr [r12], rax
.L3:
	jmp .L1
.L0:
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, esi
	mov edi, eax
	mov rax, 0
	mov rsi, rax
	mov rax, 0
	mov rdx, rax
	call wacc_createNewNode
	mov r11, rax
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov rax, r11
	mov rdi, rax
.L1:
	mov rax, rdi
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13, r14}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	mov r14, qword ptr [rsp + 16]
	add rsp, 24
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_printTree:
	push rbp
	# push {r12, r13, r14}
	sub rsp, 24
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov rbp, rsp
	cmp rdi, 0
	je .L4
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	call wacc_printTree
	mov r11d, eax
	pop rdi
	mov eax, r11d
	mov r14d, eax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	push rax
	pop rax
	mov eax, eax
	mov r14d, eax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, r14d
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	pop rdi
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, 32
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	pop rdi
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	push rax
	pop rax
	mov rax, rax
	mov r13, rax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	call wacc_printTree
	mov r11d, eax
	pop rdi
	mov eax, r11d
	mov r14d, eax
	mov eax, 0
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13, r14}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	mov r14, qword ptr [rsp + 16]
	add rsp, 24
	pop rbp
	ret
	jmp .L5
.L4:
	mov eax, 0
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13, r14}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	mov r14, qword ptr [rsp + 16]
	add rsp, 24
	pop rbp
	ret
.L5:
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
# length of .L._readi_str0
	.int 2
.L._readi_str0:
	.asciz "%d"
.text
_readi:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	# RDI contains the "original" value of the destination of the read
	# allocate space on the stack to store the read: preserve alignment!
	# the passed default argument should be stored in case of EOF
	sub rsp, 16
	mov dword ptr [rsp], edi
	lea rsi, qword ptr [rsp]
	lea rdi, [rip + .L._readi_str0]
	# on x86, al represents the number of SIMD registers used as variadic arguments
	mov al, 0
	call scanf@plt
	mov eax, dword ptr [rsp]
	add rsp, 16
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
