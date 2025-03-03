.intel_syntax noprefix
.globl main
.section .rodata
# length of .L.str0
	.int 0
.L.str0:
	.asciz ""
# length of .L.str1
	.int 43
.L.str1:
	.asciz "==========================================="
# length of .L.str2
	.int 43
.L.str2:
	.asciz "========== Hash Table Program ============="
# length of .L.str3
	.int 43
.L.str3:
	.asciz "=                                         ="
# length of .L.str4
	.int 43
.L.str4:
	.asciz "= Please choose the following options:    ="
# length of .L.str5
	.int 43
.L.str5:
	.asciz "= a: insert an integer                    ="
# length of .L.str6
	.int 43
.L.str6:
	.asciz "= b: find an integer                      ="
# length of .L.str7
	.int 43
.L.str7:
	.asciz "= c: count the integers                   ="
# length of .L.str8
	.int 43
.L.str8:
	.asciz "= d: print all integers                   ="
# length of .L.str9
	.int 43
.L.str9:
	.asciz "= e: remove an integer                    ="
# length of .L.str10
	.int 43
.L.str10:
	.asciz "= f: remove all integers                  ="
# length of .L.str11
	.int 43
.L.str11:
	.asciz "= g: exit                                 ="
# length of .L.str12
	.int 15
.L.str12:
	.asciz "Your decision: "
# length of .L.str13
	.int 18
.L.str13:
	.asciz "You have entered: "
# length of .L.str14
	.int 36
.L.str14:
	.asciz " which is invalid, please try again."
# length of .L.str15
	.int 35
.L.str15:
	.asciz "Please enter an integer to insert: "
# length of .L.str16
	.int 51
.L.str16:
	.asciz "The integer is already there. No insertion is made."
# length of .L.str17
	.int 43
.L.str17:
	.asciz "Successfully insert it. The integer is new."
# length of .L.str18
	.int 33
.L.str18:
	.asciz "Please enter an integer to find: "
# length of .L.str19
	.int 25
.L.str19:
	.asciz "The integer is not found."
# length of .L.str20
	.int 17
.L.str20:
	.asciz "Find the integer."
# length of .L.str21
	.int 10
.L.str21:
	.asciz "There are "
# length of .L.str22
	.int 10
.L.str22:
	.asciz " integers."
# length of .L.str23
	.int 24
.L.str23:
	.asciz "There is only 1 integer."
# length of .L.str24
	.int 23
.L.str24:
	.asciz "Here are the integers: "
# length of .L.str25
	.int 35
.L.str25:
	.asciz "Please enter an integer to remove: "
# length of .L.str26
	.int 29
.L.str26:
	.asciz "The integer has been removed."
# length of .L.str27
	.int 31
.L.str27:
	.asciz "All integers have been removed."
# length of .L.str28
	.int 23
.L.str28:
	.asciz "Error: unknown choice ("
# length of .L.str29
	.int 1
.L.str29:
	.asciz ")"
# length of .L.str30
	.int 13
.L.str30:
	.asciz "Goodbye Human"
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
	# 13 element array
	push rcx
	mov edi, 108
	call _malloc
	mov r11, rax
	pop rcx
	# array pointers are shifted forwards by 4 bytes (to account for size)
	mov r11, r11
	add r11, 4
	mov eax, 13
	mov dword ptr [r11 - 4], eax
	mov rax, 0
	mov qword ptr [r11], rax
	mov rax, 0
	mov qword ptr [r11 + 8], rax
	mov rax, 0
	mov qword ptr [r11 + 16], rax
	mov rax, 0
	mov qword ptr [r11 + 24], rax
	mov rax, 0
	mov qword ptr [r11 + 32], rax
	mov rax, 0
	mov qword ptr [r11 + 40], rax
	mov rax, 0
	mov qword ptr [r11 + 48], rax
	mov rax, 0
	mov qword ptr [r11 + 56], rax
	mov rax, 0
	mov qword ptr [r11 + 64], rax
	mov rax, 0
	mov qword ptr [r11 + 72], rax
	mov rax, 0
	mov qword ptr [r11 + 80], rax
	mov rax, 0
	mov qword ptr [r11 + 88], rax
	mov rax, 0
	mov qword ptr [r11 + 96], rax
	mov rax, r11
	mov r12, rax
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	call wacc_init
	mov r11b, al
	pop rcx
	mov al, r11b
	mov r13b, al
	mov al, 1
	mov r14b, al
	jmp .L39
.L40:
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	call wacc_printMenu
	mov r11b, al
	pop rcx
	mov al, r11b
	mov r15b, al
	cmp r15b, 97
	je .L41
	cmp r15b, 98
	je .L43
	cmp r15b, 99
	je .L45
	cmp r15b, 100
	je .L47
	cmp r15b, 101
	je .L49
	cmp r15b, 102
	je .L51
	cmp r15b, 103
	je .L53
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str28]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	pop rcx
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, r15b
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	pop rcx
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str29]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	pop rcx
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, -1
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _exit
	pop rcx
	jmp .L54
.L53:
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str30]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	pop rcx
	mov al, 0
	mov r14b, al
.L54:
	jmp .L52
.L51:
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	call wacc_handleMenuRemoveAll
	mov r11b, al
	pop rcx
	mov al, r11b
	mov cl, al
.L52:
	jmp .L50
.L49:
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	call wacc_handleMenuRemove
	mov r11b, al
	pop rcx
	mov al, r11b
	mov cl, al
.L50:
	jmp .L48
.L47:
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	call wacc_handleMenuPrint
	mov r11b, al
	pop rcx
	mov al, r11b
	mov cl, al
.L48:
	jmp .L46
.L45:
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	call wacc_handleMenuCount
	mov r11b, al
	pop rcx
	mov al, r11b
	mov cl, al
.L46:
	jmp .L44
.L43:
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	call wacc_handleMenuFind
	mov r11b, al
	pop rcx
	mov al, r11b
	mov cl, al
.L44:
	jmp .L42
.L41:
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	call wacc_handleMenuInsert
	mov r11b, al
	pop rcx
	mov al, r11b
	mov cl, al
.L42:
.L39:
	cmp r14b, 1
	je .L40
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

wacc_init:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	mov eax, dword ptr [rdi - 4]
	mov eax, eax
	mov r12d, eax
	mov eax, 0
	mov r13d, eax
	jmp .L0
.L1:
	mov r10d, r13d
	mov rax, 0
	mov r9, rdi
	call _arrStore8
	mov eax, r13d
	add eax, 1
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r13d, eax
.L0:
	cmp r13d, r12d
	jl .L1
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_contain:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	call wacc_calculateIndex
	mov r11d, eax
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov eax, r11d
	mov r12d, eax
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, r12d
	mov r9, rdi
	call _arrLoad8
	mov rax, r9
	mov rax, rax
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	call wacc_findNode
	mov r11, rax
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov rax, r11
	mov r13, rax
	cmp r13, 0
	setne al
	push ax
	pop ax
	mov al, al
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_insertIfNotContain:
	push rbp
	# push {r12, r13, r14}
	sub rsp, 24
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov rbp, rsp
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	call wacc_calculateIndex
	mov r11d, eax
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov eax, r11d
	mov r12d, eax
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, r12d
	mov r9, rdi
	call _arrLoad8
	mov rax, r9
	mov rax, rax
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	call wacc_findNode
	mov r11, rax
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov rax, r11
	mov r13, rax
	cmp r13, 0
	jne .L2
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	mov edi, 16
	call _malloc
	mov r11, rax
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov eax, esi
	mov qword ptr [r11], rax
	mov r10d, r12d
	mov r9, rdi
	call _arrLoad8
	mov rax, r9
	mov rax, rax
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r14, rax
	mov r10d, r12d
	mov rax, r14
	mov r9, rdi
	call _arrStore8
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13, r14}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	mov r14, qword ptr [rsp + 16]
	add rsp, 24
	pop rbp
	ret
	jmp .L3
.L2:
	mov al, 0
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13, r14}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	mov r14, qword ptr [rsp + 16]
	add rsp, 24
	pop rbp
	ret
.L3:
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_remove:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	call wacc_calculateIndex
	mov r11d, eax
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov eax, r11d
	mov r12d, eax
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, r12d
	mov r9, rdi
	call _arrLoad8
	mov rax, r9
	mov rax, rax
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	call wacc_findNode
	mov r11, rax
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov rax, r11
	mov r13, rax
	cmp r13, 0
	je .L4
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, r12d
	mov r9, rdi
	call _arrLoad8
	mov rax, r9
	mov rax, rax
	mov rdi, rax
	mov rax, r13
	mov rsi, rax
	call wacc_removeNode
	mov r11, rax
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov r10d, r12d
	mov rax, r11
	mov r9, rdi
	call _arrStore8
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret
	jmp .L5
.L4:
	mov al, 0
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret
.L5:
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_removeAll:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	mov eax, dword ptr [rdi - 4]
	mov eax, eax
	mov r12d, eax
	mov eax, 0
	mov r13d, eax
	jmp .L6
.L7:
	mov r10d, r13d
	mov r9, rdi
	call _arrLoad8
	mov rax, r9
	mov rax, rax
	mov r14, rax
	jmp .L8
.L9:
	cmp r14, 0
	je _errNull
	mov rax, qword ptr [r14 + 8]
	mov rax, rax
	mov r15, rax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _freepair
	pop rdi
	mov rax, r15
	mov r14, rax
.L8:
	cmp r14, 0
	jne .L9
	mov r10d, r13d
	mov rax, 0
	mov r9, rdi
	call _arrStore8
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
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13, r14, r15}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	mov r14, qword ptr [rsp + 16]
	mov r15, qword ptr [rsp + 24]
	add rsp, 32
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_count:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	mov eax, dword ptr [rdi - 4]
	mov eax, eax
	mov r12d, eax
	mov eax, 0
	mov r13d, eax
	mov eax, 0
	mov r14d, eax
	jmp .L10
.L11:
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, r14d
	mov r9, rdi
	call _arrLoad8
	mov rax, r9
	mov rax, rax
	mov rdi, rax
	call wacc_countNodes
	mov r11d, eax
	pop rdi
	mov eax, r11d
	mov r15d, eax
	mov eax, r13d
	add eax, r15d
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r13d, eax
	mov eax, r14d
	add eax, 1
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r14d, eax
.L10:
	cmp r14d, r12d
	jl .L11
	mov eax, r13d
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13, r14, r15}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	mov r14, qword ptr [rsp + 16]
	mov r15, qword ptr [rsp + 24]
	add rsp, 32
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_printAll:
	push rbp
	# push {r12, r13, r14}
	sub rsp, 24
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov rbp, rsp
	mov eax, dword ptr [rdi - 4]
	mov eax, eax
	mov r12d, eax
	mov eax, 0
	mov r13d, eax
	jmp .L12
.L13:
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov r10d, r13d
	mov r9, rdi
	call _arrLoad8
	mov rax, r9
	mov rax, rax
	mov rdi, rax
	call wacc_printAllNodes
	mov r11b, al
	pop rdi
	mov al, r11b
	mov r14b, al
	mov eax, r13d
	add eax, 1
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r13d, eax
.L12:
	cmp r13d, r12d
	jl .L13
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
	call _println
	pop rdi
	mov al, 1
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

wacc_calculateIndex:
	push rbp
	push r12
	mov rbp, rsp
	mov eax, dword ptr [rdi - 4]
	mov eax, eax
	mov r12d, eax
	cmp r12d, 0
	je _errDivZero
	mov eax, esi
	# sign extend EAX into EDX
	cdq
	idiv r12d
	mov eax, edx
	mov eax, eax
	push rax
	pop rax
	mov eax, eax
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_findNode:
	push rbp
	push r12
	mov rbp, rsp
	jmp .L14
.L15:
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov eax, eax
	mov r12d, eax
	cmp r12d, esi
	je .L16
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	jmp .L17
.L16:
	mov rax, rdi
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
.L17:
.L14:
	cmp rdi, 0
	jne .L15
	mov rax, 0
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_removeNode:
	push rbp
	push r12
	mov rbp, rsp
	cmp rdi, 0
	je .L18
	cmp rdi, rsi
	je .L20
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r12, rax
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	mov rax, rsi
	mov rsi, rax
	call wacc_removeNode
	mov r11, rax
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	cmp rdi, 0
	je _errNull
	mov rax, r11
	mov qword ptr [rdi + 8], rax
	mov rax, rdi
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	jmp .L21
.L20:
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rsi
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _freepair
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov rax, rdi
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
.L21:
	jmp .L19
.L18:
	mov rax, 0
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
.L19:
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_countNodes:
	push rbp
	push r12
	mov rbp, rsp
	mov eax, 0
	mov r12d, eax
	jmp .L22
.L23:
	mov eax, r12d
	add eax, 1
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov r12d, eax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
.L22:
	cmp rdi, 0
	jne .L23
	mov eax, r12d
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_printAllNodes:
	push rbp
	push r12
	mov rbp, rsp
	jmp .L24
.L25:
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov eax, eax
	mov r12d, eax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, r12d
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
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
.L24:
	cmp rdi, 0
	jne .L25
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_printMenu:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	lea rax, [rip + .L.str1]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str2]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str1]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str3]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str4]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str3]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str5]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str6]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str7]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str8]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str9]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str10]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str11]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str3]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	lea rax, [rip + .L.str1]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	mov al, 97
	movzx eax, al
	push rax
	pop rax
	mov eax, eax
	mov r12d, eax
	mov al, 103
	movzx eax, al
	push rax
	pop rax
	mov eax, eax
	mov r13d, eax
	jmp .L26
.L27:
	lea rax, [rip + .L.str12]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	mov al, 0
	mov r14b, al
	# load the current value in the destination of the read so it supports defaults
	mov al, r14b
	mov dil, al
	call _readc
	mov r11b, al
	mov al, r11b
	mov r14b, al
	movzx eax, r14b
	push rax
	pop rax
	mov eax, eax
	mov r15d, eax
	cmp r12d, r15d
	setle al
	push ax
	pop ax
	cmp al, 1
	jne .L30
	cmp r15d, r13d
	setle al
	push ax
	pop ax
	cmp al, 1
.L30:
	je .L28
	lea rax, [rip + .L.str13]
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
	lea rax, [rip + .L.str14]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	jmp .L29
.L28:
	mov al, r14b
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13, r14, r15}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	mov r14, qword ptr [rsp + 16]
	mov r15, qword ptr [rsp + 24]
	add rsp, 32
	pop rbp
	ret
.L29:
.L26:
	mov al, 1
	cmp al, 1
	je .L27
	mov al, 0
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13, r14, r15}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	mov r14, qword ptr [rsp + 16]
	mov r15, qword ptr [rsp + 24]
	add rsp, 32
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_askForInt:
	push rbp
	push r12
	mov rbp, rsp
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	pop rdi
	mov eax, 0
	mov r12d, eax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	# load the current value in the destination of the read so it supports defaults
	mov eax, r12d
	mov edi, eax
	call _readi
	mov r11d, eax
	pop rdi
	mov eax, r11d
	mov r12d, eax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str13]
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
	mov eax, r12d
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	pop rdi
	mov eax, r12d
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_handleMenuInsert:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str15]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	call wacc_askForInt
	mov r11d, eax
	pop rdi
	mov eax, r11d
	mov r12d, eax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	mov eax, r12d
	mov esi, eax
	call wacc_insertIfNotContain
	mov r11b, al
	pop rdi
	mov al, r11b
	mov r13b, al
	cmp r13b, 1
	je .L31
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str16]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	pop rdi
	jmp .L32
.L31:
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str17]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	pop rdi
.L32:
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_handleMenuFind:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str18]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	call wacc_askForInt
	mov r11d, eax
	pop rdi
	mov eax, r11d
	mov r12d, eax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	mov eax, r12d
	mov esi, eax
	call wacc_contain
	mov r11b, al
	pop rdi
	mov al, r11b
	mov r13b, al
	cmp r13b, 1
	je .L33
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str19]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	pop rdi
	jmp .L34
.L33:
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str20]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	pop rdi
.L34:
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_handleMenuCount:
	push rbp
	push r12
	mov rbp, rsp
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	call wacc_count
	mov r11d, eax
	pop rdi
	mov eax, r11d
	mov r12d, eax
	cmp r12d, 1
	je .L35
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str21]
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
	mov eax, r12d
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	pop rdi
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str22]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	pop rdi
	jmp .L36
.L35:
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str23]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	pop rdi
.L36:
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_handleMenuPrint:
	push rbp
	push r12
	mov rbp, rsp
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str24]
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
	mov rax, rdi
	mov rdi, rax
	call wacc_printAll
	mov r11b, al
	pop rdi
	mov al, r11b
	mov r12b, al
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_handleMenuRemove:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str25]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	call wacc_askForInt
	mov r11d, eax
	pop rdi
	mov eax, r11d
	mov r12d, eax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	mov eax, r12d
	mov esi, eax
	call wacc_remove
	mov r11b, al
	pop rdi
	mov al, r11b
	mov r13b, al
	cmp r13b, 1
	je .L37
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str19]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	pop rdi
	jmp .L38
.L37:
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str26]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	pop rdi
.L38:
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_handleMenuRemoveAll:
	push rbp
	push r12
	mov rbp, rsp
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	call wacc_removeAll
	mov r11b, al
	pop rdi
	mov al, r11b
	mov r12b, al
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str27]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	pop rdi
	mov al, 1
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

.section .rodata
# length of .L._readc_str0
	.int 3
.L._readc_str0:
	.asciz " %c"
.text
_readc:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	# RDI contains the "original" value of the destination of the read
	# allocate space on the stack to store the read: preserve alignment!
	# the passed default argument should be stored in case of EOF
	sub rsp, 16
	mov byte ptr [rsp], dil
	lea rsi, qword ptr [rsp]
	lea rdi, [rip + .L._readc_str0]
	# on x86, al represents the number of SIMD registers used as variadic arguments
	mov al, 0
	call scanf@plt
	mov al, byte ptr [rsp]
	add rsp, 16
	mov rsp, rbp
	pop rbp
	ret

_exit:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	call exit@plt
	mov rsp, rbp
	pop rbp
	ret

_freepair:
	push rbp
	mov rbp, rsp
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	cmp rdi, 0
	je _errNull
	call free@plt
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

_arrLoad8:
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
	mov r9, qword ptr [r9 + 8*r10]
	pop rbx
	ret

_arrStore8:
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
	mov qword ptr [r9 + 8*r10], rax
	pop rbx
	ret

.section .rodata
# length of .L._errDivZero_str0
	.int 40
.L._errDivZero_str0:
	.asciz "fatal error: division or modulo by zero\n"
.text
_errDivZero:
	# external calls must be stack-aligned to 16 bytes, accomplished by masking with fffffffffffffff0
	and rsp, -16
	lea rdi, [rip + .L._errDivZero_str0]
	call _prints
	mov dil, -1
	call exit@plt

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
