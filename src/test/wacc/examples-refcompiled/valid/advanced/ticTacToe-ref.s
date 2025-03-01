.intel_syntax noprefix
.globl main
.section .rodata
# length of .L.str0
	.int 38
.L.str0:
	.asciz "========= Tic Tac Toe ================"
# length of .L.str1
	.int 38
.L.str1:
	.asciz "=  Because we know you want to win   ="
# length of .L.str2
	.int 38
.L.str2:
	.asciz "======================================"
# length of .L.str3
	.int 38
.L.str3:
	.asciz "=                                    ="
# length of .L.str4
	.int 38
.L.str4:
	.asciz "= Who would you like to be?          ="
# length of .L.str5
	.int 38
.L.str5:
	.asciz "=   x  (play first)                  ="
# length of .L.str6
	.int 38
.L.str6:
	.asciz "=   o  (play second)                 ="
# length of .L.str7
	.int 38
.L.str7:
	.asciz "=   q  (quit)                        ="
# length of .L.str8
	.int 39
.L.str8:
	.asciz "Which symbol you would like to choose: "
# length of .L.str9
	.int 16
.L.str9:
	.asciz "Invalid symbol: "
# length of .L.str10
	.int 17
.L.str10:
	.asciz "Please try again."
# length of .L.str11
	.int 15
.L.str11:
	.asciz "Goodbye safety."
# length of .L.str12
	.int 17
.L.str12:
	.asciz "You have chosen: "
# length of .L.str13
	.int 6
.L.str13:
	.asciz " 1 2 3"
# length of .L.str14
	.int 1
.L.str14:
	.asciz "1"
# length of .L.str15
	.int 6
.L.str15:
	.asciz " -+-+-"
# length of .L.str16
	.int 1
.L.str16:
	.asciz "2"
# length of .L.str17
	.int 1
.L.str17:
	.asciz "3"
# length of .L.str18
	.int 0
.L.str18:
	.asciz ""
# length of .L.str19
	.int 23
.L.str19:
	.asciz "What is your next move?"
# length of .L.str20
	.int 12
.L.str20:
	.asciz " row (1-3): "
# length of .L.str21
	.int 15
.L.str21:
	.asciz " column (1-3): "
# length of .L.str22
	.int 39
.L.str22:
	.asciz "Your move is invalid. Please try again."
# length of .L.str23
	.int 21
.L.str23:
	.asciz "The AI played at row "
# length of .L.str24
	.int 8
.L.str24:
	.asciz " column "
# length of .L.str25
	.int 31
.L.str25:
	.asciz "AI is cleaning up its memory..."
# length of .L.str26
	.int 52
.L.str26:
	.asciz "Internal Error: cannot find the next move for the AI"
# length of .L.str27
	.int 50
.L.str27:
	.asciz "Internal Error: symbol given is neither 'x' or 'o'"
# length of .L.str28
	.int 58
.L.str28:
	.asciz "Initialising AI. Please wait, this may take a few minutes."
# length of .L.str29
	.int 10
.L.str29:
	.asciz "Stalemate!"
# length of .L.str30
	.int 9
.L.str30:
	.asciz " has won!"
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
	call wacc_chooseSymbol
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r12b, al
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
	mov al, r12b
	mov dil, al
	call wacc_oppositeSymbol
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r13b, al
	mov al, 120
	mov r14b, al
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
	call wacc_allocateNewBoard
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov rax, r11
	mov r15, rax
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
	lea rax, [rip + .L.str28]
	push rax
	pop rax
	mov rax, rax
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
	mov al, r13b
	mov dil, al
	call wacc_initAI
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov rax, r11
	mov rcx, rax
	mov eax, 0
	mov edx, eax
	mov al, 0
	mov sil, al
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
	mov rax, r15
	mov rdi, rax
	call wacc_printBoard
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov dil, al
	jmp .L135
.L136:
	# 2 element array
	# push {rcx, rdx, rsi, rdi, r8, r9}
	sub rsp, 48
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	mov qword ptr [rsp + 40], r9
	mov edi, 12
	call _malloc
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	# array pointers are shifted forwards by 4 bytes (to account for size)
	mov r11, r11
	add r11, 4
	mov eax, 2
	mov dword ptr [r11 - 4], eax
	mov eax, 0
	mov dword ptr [r11], eax
	mov eax, 0
	mov dword ptr [r11 + 4], eax
	mov rax, r11
	mov r8, rax
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
	mov rax, r15
	mov rdi, rax
	mov al, r14b
	mov sil, al
	mov al, r12b
	mov dl, al
	mov rax, rcx
	mov rcx, rax
	mov rax, r8
	mov r8, rax
	call wacc_askForAMove
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov dil, al
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
	mov rax, r15
	mov rdi, rax
	mov al, r14b
	mov sil, al
	mov r10d, 0
	push r9
	mov r9, r8
	call _arrLoad4
	mov eax, r9d
	pop r9
	mov eax, eax
	mov edx, eax
	mov r10d, 1
	push r9
	mov r9, r8
	call _arrLoad4
	mov eax, r9d
	pop r9
	mov eax, eax
	mov ecx, eax
	call wacc_placeMove
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov dil, al
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
	mov rax, r15
	mov rdi, rax
	mov al, r14b
	mov sil, al
	mov al, r12b
	mov dl, al
	mov rax, rcx
	mov rcx, rax
	mov r10d, 0
	push r9
	mov r9, r8
	call _arrLoad4
	mov eax, r9d
	pop r9
	mov eax, eax
	mov r8d, eax
	mov r10d, 1
	mov rax, qword ptr [r11 + 32]
	push r9
	mov r9, rax
	call _arrLoad4
	mov eax, r9d
	pop r9
	mov eax, eax
	mov r9d, eax
	call wacc_notifyMove
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov dil, al
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
	mov rax, r15
	mov rdi, rax
	call wacc_printBoard
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov dil, al
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
	mov rax, r15
	mov rdi, rax
	mov al, r14b
	mov sil, al
	call wacc_hasWon
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r9b, al
	cmp r9b, 1
	je .L137
	jmp .L138
.L137:
	mov al, r14b
	mov sil, al
.L138:
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
	mov al, r14b
	mov dil, al
	call wacc_oppositeSymbol
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r14b, al
	mov eax, edx
	add eax, 1
	jo _errOverflow
	push rax
	pop rax
	mov eax, eax
	mov edx, eax
.L135:
	cmp sil, 0
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L139
	cmp edx, 9
	setl al
	push ax
	pop ax
	cmp al, 1
.L139:
	je .L136
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
	mov rax, r15
	mov rdi, rax
	call wacc_freeBoard
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov dil, al
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
	call wacc_destroyAI
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov dil, al
	cmp sil, 0
	jne .L140
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
	lea rax, [rip + .L.str29]
	push rax
	pop rax
	mov rax, rax
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
	jmp .L141
.L140:
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
	call _printc
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
	lea rax, [rip + .L.str30]
	push rax
	pop rax
	mov rax, rax
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
.L141:
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

wacc_chooseSymbol:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	lea rax, [rip + .L.str0]
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
	lea rax, [rip + .L.str2]
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
	lea rax, [rip + .L.str3]
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
	mov al, 0
	mov r12b, al
	jmp .L0
.L1:
	lea rax, [rip + .L.str8]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	mov al, 0
	mov r13b, al
	# load the current value in the destination of the read so it supports defaults
	mov al, r13b
	mov dil, al
	call _readc
	mov r11b, al
	mov al, r11b
	mov r13b, al
	cmp r13b, 120
	sete al
	push ax
	pop ax
	cmp al, 1
	je .L4
	cmp r13b, 88
	sete al
	push ax
	pop ax
	cmp al, 1
.L4:
	je .L2
	cmp r13b, 111
	sete al
	push ax
	pop ax
	cmp al, 1
	je .L7
	cmp r13b, 79
	sete al
	push ax
	pop ax
	cmp al, 1
.L7:
	je .L5
	cmp r13b, 113
	sete al
	push ax
	pop ax
	cmp al, 1
	je .L10
	cmp r13b, 81
	sete al
	push ax
	pop ax
	cmp al, 1
.L10:
	je .L8
	lea rax, [rip + .L.str9]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	mov al, r13b
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	call _println
	lea rax, [rip + .L.str10]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	jmp .L9
.L8:
	lea rax, [rip + .L.str11]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	mov eax, 0
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _exit
.L9:
	jmp .L6
.L5:
	mov al, 111
	mov r12b, al
.L6:
	jmp .L3
.L2:
	mov al, 120
	mov r12b, al
.L3:
.L0:
	cmp r12b, 0
	je .L1
	lea rax, [rip + .L.str12]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	mov al, r12b
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	call _println
	mov al, r12b
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_printBoard:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str13]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str14]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	call wacc_printRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str15]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str16]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	call wacc_printRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str15]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str17]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r15
	mov rdi, rax
	call wacc_printRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str18]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
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

wacc_printRow:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov al, al
	mov r13b, al
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov al, al
	mov r14b, al
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov al, al
	mov r15b, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, r13b
	mov dil, al
	call wacc_printCell
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, 124
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, r14b
	mov dil, al
	call wacc_printCell
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, 124
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, r15b
	mov dil, al
	call wacc_printCell
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str18]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
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

wacc_printCell:
	push rbp
	mov rbp, rsp
	cmp dil, 0
	je .L11
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, dil
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	pop rdi
	jmp .L12
.L11:
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, 32
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	pop rdi
.L12:
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_askForAMoveHuman:
	push rbp
	# push {r12, r13, r14}
	sub rsp, 24
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov rbp, rsp
	mov al, 0
	mov r12b, al
	mov eax, 0
	mov r13d, eax
	mov eax, 0
	mov r14d, eax
	jmp .L13
.L14:
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
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
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str20]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	# load the current value in the destination of the read so it supports defaults
	mov eax, r13d
	mov edi, eax
	call _readi
	mov r11d, eax
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov eax, r11d
	mov r13d, eax
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str21]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	# load the current value in the destination of the read so it supports defaults
	mov eax, r14d
	mov edi, eax
	call _readi
	mov r11d, eax
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov eax, r11d
	mov r14d, eax
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	mov eax, r13d
	mov esi, eax
	mov eax, r14d
	mov edx, eax
	call wacc_validateMove
	mov r11b, al
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov r12b, al
	cmp r12b, 1
	je .L15
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
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
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	jmp .L16
.L15:
	# push {rsi, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rsi
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str18]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	call _println
	# pop/peek {rsi, rdi}
	mov rsi, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov r10d, 0
	mov eax, r13d
	mov r9, rsi
	call _arrStore4
	mov r10d, 1
	mov eax, r14d
	mov r9, rsi
	call _arrStore4
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
.L16:
.L13:
	cmp r12b, 1
	jne .L14
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

wacc_validateMove:
	push rbp
	push r12
	mov rbp, rsp
	mov eax, 1
	cmp eax, esi
	setle al
	push ax
	pop ax
	cmp al, 1
	jne .L19
	cmp esi, 3
	setle al
	push ax
	pop ax
	cmp al, 1
	jne .L20
	mov eax, 1
	cmp eax, edx
	setle al
	push ax
	pop ax
	cmp al, 1
	jne .L21
	cmp edx, 3
	setle al
	push ax
	pop ax
	cmp al, 1
.L21:
	sete al
	push ax
	pop ax
	cmp al, 1
.L20:
	sete al
	push ax
	pop ax
	cmp al, 1
.L19:
	je .L17
	mov al, 0
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	jmp .L18
.L17:
	# push {rdx, rsi, rdi}
	sub rsp, 24
	mov qword ptr [rsp], rdx
	mov qword ptr [rsp + 8], rsi
	mov qword ptr [rsp + 16], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	mov eax, edx
	mov edx, eax
	call wacc_symbolAt
	mov r11b, al
	# pop/peek {rdx, rsi, rdi}
	mov rdx, qword ptr [rsp]
	mov rsi, qword ptr [rsp + 8]
	mov rdi, qword ptr [rsp + 16]
	add rsp, 24
	mov al, r11b
	mov r12b, al
	cmp r12b, 0
	sete al
	push ax
	pop ax
	mov al, al
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
.L18:
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_notifyMoveHuman:
	push rbp
	mov rbp, rsp
	# push {rcx, rdx, rsi, rdi, r8}
	sub rsp, 40
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str23]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	add rsp, 40
	# push {rcx, rdx, rsi, rdi, r8}
	sub rsp, 40
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, ecx
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	# pop/peek {rcx, rdx, rsi, rdi, r8}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	add rsp, 40
	# push {rcx, rdx, rsi, rdi, r8}
	sub rsp, 40
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	lea rax, [rip + .L.str24]
	push rax
	pop rax
	mov rax, rax
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _prints
	# pop/peek {rcx, rdx, rsi, rdi, r8}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	add rsp, 40
	# push {rcx, rdx, rsi, rdi, r8}
	sub rsp, 40
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, r8d
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	# pop/peek {rcx, rdx, rsi, rdi, r8}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	add rsp, 40
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_initAI:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	push rdi
	mov edi, 16
	call _malloc
	mov r11, rax
	pop rdi
	mov al, dil
	mov qword ptr [r11], rax
	mov rax, 0
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r12, rax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, dil
	mov dil, al
	call wacc_generateAllPossibleStates
	mov r11, rax
	pop rdi
	mov rax, r11
	mov r13, rax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	mov rax, qword ptr [r11]
	mov al, al
	mov sil, al
	mov al, 120
	mov dl, al
	call wacc_setValuesForAllStates
	mov r11d, eax
	pop rdi
	mov eax, r11d
	mov r14d, eax
	push rdi
	mov edi, 16
	call _malloc
	mov r11, rax
	pop rdi
	mov rax, r12
	mov qword ptr [r11], rax
	mov rax, r13
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r15, rax
	mov rax, r15
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

wacc_generateAllPossibleStates:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	call wacc_allocateNewBoard
	mov r11, rax
	pop rdi
	mov rax, r11
	mov r12, rax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	call wacc_convertFromBoardToState
	mov r11, rax
	pop rdi
	mov rax, r11
	mov r13, rax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	mov al, 120
	mov sil, al
	call wacc_generateNextStates
	mov r11, rax
	pop rdi
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

wacc_convertFromBoardToState:
	push rbp
	# push {r12, r13, r14}
	sub rsp, 24
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov rbp, rsp
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	call wacc_generateEmptyPointerBoard
	mov r11, rax
	pop rdi
	mov rax, r11
	mov r12, rax
	push rdi
	mov edi, 16
	call _malloc
	mov r11, rax
	pop rdi
	mov rax, rdi
	mov qword ptr [r11], rax
	mov rax, r12
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r13, rax
	push rdi
	mov edi, 16
	call _malloc
	mov r11, rax
	pop rdi
	mov rax, r13
	mov qword ptr [r11], rax
	mov eax, 0
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r14, rax
	mov rax, r14
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

wacc_generateEmptyPointerBoard:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	call wacc_generateEmptyPointerRow
	mov r11, rax
	pop rcx
	mov rax, r11
	mov r12, rax
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	call wacc_generateEmptyPointerRow
	mov r11, rax
	pop rcx
	mov rax, r11
	mov r13, rax
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	call wacc_generateEmptyPointerRow
	mov r11, rax
	pop rcx
	mov rax, r11
	mov r14, rax
	push rcx
	mov edi, 16
	call _malloc
	mov r11, rax
	pop rcx
	mov rax, r12
	mov qword ptr [r11], rax
	mov rax, r13
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r15, rax
	push rcx
	mov edi, 16
	call _malloc
	mov r11, rax
	pop rcx
	mov rax, r15
	mov qword ptr [r11], rax
	mov rax, r14
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov rcx, rax
	mov rax, rcx
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

wacc_generateEmptyPointerRow:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	mov edi, 16
	call _malloc
	mov r11, rax
	mov rax, 0
	mov qword ptr [r11], rax
	mov rax, 0
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r12, rax
	mov edi, 16
	call _malloc
	mov r11, rax
	mov rax, r12
	mov qword ptr [r11], rax
	mov rax, 0
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

wacc_generateNextStates:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	# push {rcx, rdx, rsi, rdi}
	sub rsp, 32
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, sil
	mov dil, al
	call wacc_oppositeSymbol
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	add rsp, 32
	mov al, r11b
	mov r15b, al
	# push {rcx, rdx, rsi, rdi}
	sub rsp, 32
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	mov al, r15b
	mov sil, al
	call wacc_hasWon
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	add rsp, 32
	mov al, r11b
	mov cl, al
	cmp cl, 1
	je .L22
	# push {rcx, rdx, rsi, rdi}
	sub rsp, 32
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	mov rax, r14
	mov rsi, rax
	mov rax, qword ptr [r11 + 16]
	mov al, al
	mov dl, al
	call wacc_generateNextStatesBoard
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	add rsp, 32
	mov al, r11b
	mov dl, al
	mov rax, rdi
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
	jmp .L23
.L22:
	mov rax, rdi
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
.L23:
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_generateNextStatesBoard:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	sub rsp, 9
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
	cmp rsi, 0
	je _errNull
	mov rax, qword ptr [rsi]
	mov rax, rax
	mov rcx, rax
	cmp rcx, 0
	je _errNull
	mov rax, qword ptr [rcx]
	mov rax, rax
	mov r8, rax
	cmp rcx, 0
	je _errNull
	mov rax, qword ptr [rcx + 8]
	mov rax, rax
	mov r9, rax
	cmp rsi, 0
	je _errNull
	mov rax, qword ptr [rsi + 8]
	mov rax, rax
	mov qword ptr [rbp - 9], rax
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
	mov rax, rdi
	mov rdi, rax
	mov rax, r13
	mov rsi, rax
	mov rax, r8
	mov rdx, rax
	mov rax, qword ptr [r11 + 8]
	mov al, al
	mov cl, al
	mov eax, 1
	mov r8d, eax
	call wacc_generateNextStatesRow
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov byte ptr [rbp - 1], al
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
	mov rax, rdi
	mov rdi, rax
	mov rax, r14
	mov rsi, rax
	mov rax, r9
	mov rdx, rax
	mov rax, qword ptr [r11 + 8]
	mov al, al
	mov cl, al
	mov eax, 2
	mov r8d, eax
	call wacc_generateNextStatesRow
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov byte ptr [rbp - 1], al
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
	mov rax, rdi
	mov rdi, rax
	mov rax, r15
	mov rsi, rax
	mov rax, qword ptr [rbp - 9]
	mov rax, rax
	mov rdx, rax
	mov rax, qword ptr [r11 + 8]
	mov al, al
	mov cl, al
	mov eax, 3
	mov r8d, eax
	call wacc_generateNextStatesRow
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov byte ptr [rbp - 1], al
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
	add rsp, 9
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_generateNextStatesRow:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rsi, 0
	je _errNull
	mov rax, qword ptr [rsi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov al, al
	mov r13b, al
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov al, al
	mov r14b, al
	cmp rsi, 0
	je _errNull
	mov rax, qword ptr [rsi + 8]
	mov al, al
	mov r15b, al
	cmp rdx, 0
	je _errNull
	mov rax, qword ptr [rdx]
	mov rax, rax
	mov r9, rax
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
	mov rax, rdi
	mov rdi, rax
	mov al, r13b
	mov sil, al
	mov al, cl
	mov dl, al
	mov eax, r8d
	mov ecx, eax
	mov eax, 1
	mov r8d, eax
	call wacc_generateNextStatesCell
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	cmp r9, 0
	je _errNull
	mov rax, r11
	mov qword ptr [r9], rax
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
	mov rax, rdi
	mov rdi, rax
	mov al, r14b
	mov sil, al
	mov al, cl
	mov dl, al
	mov eax, r8d
	mov ecx, eax
	mov eax, 2
	mov r8d, eax
	call wacc_generateNextStatesCell
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	cmp r9, 0
	je _errNull
	mov rax, r11
	mov qword ptr [r9 + 8], rax
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
	mov rax, rdi
	mov rdi, rax
	mov al, r15b
	mov sil, al
	mov al, cl
	mov dl, al
	mov eax, r8d
	mov ecx, eax
	mov eax, 3
	mov r8d, eax
	call wacc_generateNextStatesCell
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	cmp rdx, 0
	je _errNull
	mov rax, r11
	mov qword ptr [rdx + 8], rax
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

wacc_generateNextStatesCell:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp sil, 0
	je .L24
	mov rax, 0
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
	jmp .L25
.L24:
	# push {rcx, rdx, rsi, rdi, r8}
	sub rsp, 40
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	call wacc_cloneBoard
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	add rsp, 40
	mov rax, r11
	mov r12, rax
	# push {rcx, rdx, rsi, rdi, r8}
	sub rsp, 40
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	mov al, dl
	mov sil, al
	mov eax, ecx
	mov edx, eax
	mov eax, r8d
	mov ecx, eax
	call wacc_placeMove
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	add rsp, 40
	mov al, r11b
	mov r13b, al
	# push {rcx, rdx, rsi, rdi, r8}
	sub rsp, 40
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	call wacc_convertFromBoardToState
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	add rsp, 40
	mov rax, r11
	mov r14, rax
	# push {rcx, rdx, rsi, rdi, r8}
	sub rsp, 40
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, dl
	mov dil, al
	call wacc_oppositeSymbol
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	add rsp, 40
	mov al, r11b
	mov r15b, al
	# push {rcx, rdx, rsi, rdi, r8}
	sub rsp, 40
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	mov al, r15b
	mov sil, al
	call wacc_generateNextStates
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	add rsp, 40
	mov rax, r11
	mov r14, rax
	mov rax, r14
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
.L25:
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_cloneBoard:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	call wacc_allocateNewBoard
	mov r11, rax
	pop rdi
	mov rax, r11
	mov r12, rax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	mov rax, r12
	mov rsi, rax
	call wacc_copyBoard
	mov r11b, al
	pop rdi
	mov al, r11b
	mov r13b, al
	mov rax, r12
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_copyBoard:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	sub rsp, 1
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
	cmp rsi, 0
	je _errNull
	mov rax, qword ptr [rsi]
	mov rax, rax
	mov rcx, rax
	cmp rcx, 0
	je _errNull
	mov rax, qword ptr [rcx]
	mov rax, rax
	mov rdx, rax
	cmp rcx, 0
	je _errNull
	mov rax, qword ptr [rcx + 8]
	mov rax, rax
	mov r8, rax
	cmp rsi, 0
	je _errNull
	mov rax, qword ptr [rsi + 8]
	mov rax, rax
	mov r9, rax
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
	mov rax, r13
	mov rdi, rax
	mov rax, rdx
	mov rsi, rax
	call wacc_copyRow
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov byte ptr [rbp - 1], al
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
	mov rax, r14
	mov rdi, rax
	mov rax, r8
	mov rsi, rax
	call wacc_copyRow
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov byte ptr [rbp - 1], al
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
	mov rax, r15
	mov rdi, rax
	mov rax, r9
	mov rsi, rax
	call wacc_copyRow
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov byte ptr [rbp - 1], al
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
	add rsp, 1
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_copyRow:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp rsi, 0
	je _errNull
	mov rax, qword ptr [rsi]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	push rax
	cmp r13, 0
	je _errNull
	pop rax
	mov al, al
	mov qword ptr [r13], rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	push rax
	cmp r13, 0
	je _errNull
	pop rax
	mov al, al
	mov qword ptr [r13 + 8], rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	push rax
	cmp rsi, 0
	je _errNull
	pop rax
	mov al, al
	mov qword ptr [rsi + 8], rax
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

wacc_setValuesForAllStates:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	mov eax, 0
	mov r12d, eax
	cmp rdi, 0
	je .L26
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r13, rax
	cmp r13, 0
	je _errNull
	mov rax, qword ptr [r13]
	mov rax, rax
	mov r14, rax
	cmp r13, 0
	je _errNull
	mov rax, qword ptr [r13 + 8]
	mov rax, rax
	mov r15, rax
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
	call wacc_oppositeSymbol
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov cl, al
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
	mov rax, r14
	mov rdi, rax
	mov al, cl
	mov sil, al
	call wacc_hasWon
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r8b, al
	cmp r8b, 1
	je .L28
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
	mov rax, r14
	mov rdi, rax
	call wacc_containEmptyCell
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r9b, al
	cmp r9b, 1
	je .L30
	mov eax, 0
	mov r12d, eax
	jmp .L31
.L30:
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
	mov rax, r15
	mov rdi, rax
	mov al, sil
	mov sil, al
	mov al, cl
	mov dl, al
	call wacc_calculateValuesFromNextStates
	mov r11d, eax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov eax, r11d
	mov r12d, eax
	cmp r12d, 100
	je .L32
	jmp .L33
.L32:
	mov eax, 90
	mov r12d, eax
.L33:
.L31:
	jmp .L29
.L28:
	cmp cl, sil
	je .L34
	mov eax, -100
	mov r12d, eax
	jmp .L35
.L34:
	mov eax, 100
	mov r12d, eax
.L35:
.L29:
	cmp rdi, 0
	je _errNull
	mov eax, r12d
	mov qword ptr [rdi + 8], rax
	jmp .L27
.L26:
	cmp dl, sil
	je .L36
	mov eax, -101
	mov r12d, eax
	jmp .L37
.L36:
	mov eax, 101
	mov r12d, eax
.L37:
.L27:
	mov eax, r12d
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

wacc_calculateValuesFromNextStates:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	sub rsp, 4
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
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
	mov rax, r13
	mov rdi, rax
	mov al, sil
	mov sil, al
	mov al, dl
	mov dl, al
	call wacc_calculateValuesFromNextStatesRow
	mov r11d, eax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov eax, r11d
	mov ecx, eax
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
	mov rax, r14
	mov rdi, rax
	mov al, sil
	mov sil, al
	mov al, dl
	mov dl, al
	call wacc_calculateValuesFromNextStatesRow
	mov r11d, eax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov eax, r11d
	mov r8d, eax
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
	mov rax, r15
	mov rdi, rax
	mov al, sil
	mov sil, al
	mov al, dl
	mov dl, al
	call wacc_calculateValuesFromNextStatesRow
	mov r11d, eax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov eax, r11d
	mov r9d, eax
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
	mov al, dl
	mov sil, al
	mov eax, ecx
	mov edx, eax
	mov eax, r8d
	mov ecx, eax
	mov eax, r9d
	mov r8d, eax
	call wacc_combineValue
	mov r11d, eax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov eax, r11d
	mov dword ptr [rbp - 4], eax
	mov eax, dword ptr [rbp - 4]
	mov eax, eax
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
	add rsp, 4
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_calculateValuesFromNextStatesRow:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	sub rsp, 4
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
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
	mov rax, r13
	mov rdi, rax
	mov al, sil
	mov sil, al
	mov al, dl
	mov dl, al
	call wacc_setValuesForAllStates
	mov r11d, eax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov eax, r11d
	mov ecx, eax
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
	mov rax, r14
	mov rdi, rax
	mov al, sil
	mov sil, al
	mov al, dl
	mov dl, al
	call wacc_setValuesForAllStates
	mov r11d, eax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov eax, r11d
	mov r8d, eax
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
	mov rax, r15
	mov rdi, rax
	mov al, sil
	mov sil, al
	mov al, dl
	mov dl, al
	call wacc_setValuesForAllStates
	mov r11d, eax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov eax, r11d
	mov r9d, eax
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
	mov al, dl
	mov sil, al
	mov eax, ecx
	mov edx, eax
	mov eax, r8d
	mov ecx, eax
	mov eax, r9d
	mov r8d, eax
	call wacc_combineValue
	mov r11d, eax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov eax, r11d
	mov dword ptr [rbp - 4], eax
	mov eax, dword ptr [rbp - 4]
	mov eax, eax
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
	add rsp, 4
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_combineValue:
	push rbp
	push r12
	mov rbp, rsp
	mov eax, 0
	mov r12d, eax
	cmp dil, sil
	je .L38
	# push {rcx, rdx, rsi, rdi, r8}
	sub rsp, 40
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, edx
	mov edi, eax
	mov eax, ecx
	mov esi, eax
	mov eax, r8d
	mov edx, eax
	call wacc_max3
	mov r11d, eax
	# pop/peek {rcx, rdx, rsi, rdi, r8}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	add rsp, 40
	mov eax, r11d
	mov r12d, eax
	jmp .L39
.L38:
	# push {rcx, rdx, rsi, rdi, r8}
	sub rsp, 40
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, edx
	mov edi, eax
	mov eax, ecx
	mov esi, eax
	mov eax, r8d
	mov edx, eax
	call wacc_min3
	mov r11d, eax
	# pop/peek {rcx, rdx, rsi, rdi, r8}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	add rsp, 40
	mov eax, r11d
	mov r12d, eax
.L39:
	mov eax, r12d
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_min3:
	push rbp
	mov rbp, rsp
	cmp edi, esi
	jl .L40
	cmp esi, edx
	jl .L42
	mov eax, edx
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop rbp
	ret
	jmp .L43
.L42:
	mov eax, esi
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop rbp
	ret
.L43:
	jmp .L41
.L40:
	cmp edi, edx
	jl .L44
	mov eax, edx
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop rbp
	ret
	jmp .L45
.L44:
	mov eax, edi
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop rbp
	ret
.L45:
.L41:
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_max3:
	push rbp
	mov rbp, rsp
	cmp edi, esi
	jg .L46
	cmp esi, edx
	jg .L48
	mov eax, edx
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop rbp
	ret
	jmp .L49
.L48:
	mov eax, esi
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop rbp
	ret
.L49:
	jmp .L47
.L46:
	cmp edi, edx
	jg .L50
	mov eax, edx
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop rbp
	ret
	jmp .L51
.L50:
	mov eax, edi
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop rbp
	ret
.L51:
.L47:
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_destroyAI:
	push rbp
	# push {r12, r13, r14}
	sub rsp, 24
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r13, rax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	call wacc_deleteStateTreeRecursively
	mov r11b, al
	pop rdi
	mov al, r11b
	mov r14b, al
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _freepair
	pop rdi
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _freepair
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

wacc_askForAMoveAI:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	sub rsp, 1
	cmp rcx, 0
	je _errNull
	mov rax, qword ptr [rcx]
	mov rax, rax
	mov r12, rax
	cmp rcx, 0
	je _errNull
	mov rax, qword ptr [rcx + 8]
	mov rax, rax
	mov r13, rax
	cmp r13, 0
	je _errNull
	mov rax, qword ptr [r13]
	mov rax, rax
	mov r14, rax
	cmp r14, 0
	je _errNull
	mov rax, qword ptr [r14 + 8]
	mov rax, rax
	mov r15, rax
	cmp r13, 0
	je _errNull
	mov rax, qword ptr [r13 + 8]
	mov eax, eax
	mov r9d, eax
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
	mov rax, r15
	mov rdi, rax
	mov eax, r9d
	mov esi, eax
	mov rax, r8
	mov rdx, rax
	call wacc_findTheBestMove
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov byte ptr [rbp - 1], al
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
	lea rax, [rip + .L.str25]
	push rax
	pop rax
	mov rax, rax
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
	mov rax, r15
	mov rdi, rax
	mov r10d, 0
	push r9
	mov r9, r8
	call _arrLoad4
	mov eax, r9d
	pop r9
	mov eax, eax
	mov esi, eax
	mov r10d, 1
	push r9
	mov r9, r8
	call _arrLoad4
	mov eax, r9d
	pop r9
	mov eax, eax
	mov edx, eax
	call wacc_deleteAllOtherChildren
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	cmp rcx, 0
	je _errNull
	mov rax, r11
	mov qword ptr [rcx + 8], rax
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
	mov rax, r13
	mov rdi, rax
	call wacc_deleteThisStateOnly
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov byte ptr [rbp - 1], al
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
	add rsp, 1
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_findTheBestMove:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	cmp esi, 90
	je .L52
	jmp .L53
.L52:
	# push {rdx, rsi, rdi}
	sub rsp, 24
	mov qword ptr [rsp], rdx
	mov qword ptr [rsp + 8], rsi
	mov qword ptr [rsp + 16], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	mov eax, 100
	mov esi, eax
	mov rax, rdx
	mov rdx, rax
	call wacc_findMoveWithGivenValue
	mov r11b, al
	# pop/peek {rdx, rsi, rdi}
	mov rdx, qword ptr [rsp]
	mov rsi, qword ptr [rsp + 8]
	mov rdi, qword ptr [rsp + 16]
	add rsp, 24
	mov al, r11b
	mov r13b, al
	cmp r13b, 1
	je .L54
	jmp .L55
.L54:
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret
.L55:
.L53:
	# push {rdx, rsi, rdi}
	sub rsp, 24
	mov qword ptr [rsp], rdx
	mov qword ptr [rsp + 8], rsi
	mov qword ptr [rsp + 16], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	mov rax, rdx
	mov rdx, rax
	call wacc_findMoveWithGivenValue
	mov r11b, al
	# pop/peek {rdx, rsi, rdi}
	mov rdx, qword ptr [rsp]
	mov rsi, qword ptr [rsp + 8]
	mov rdi, qword ptr [rsp + 16]
	add rsp, 24
	mov al, r11b
	mov r12b, al
	cmp r12b, 1
	je .L56
	# push {rdx, rsi, rdi}
	sub rsp, 24
	mov qword ptr [rsp], rdx
	mov qword ptr [rsp + 8], rsi
	mov qword ptr [rsp + 16], rdi
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
	# pop/peek {rdx, rsi, rdi}
	mov rdx, qword ptr [rsp]
	mov rsi, qword ptr [rsp + 8]
	mov rdi, qword ptr [rsp + 16]
	add rsp, 24
	# push {rdx, rsi, rdi}
	sub rsp, 24
	mov qword ptr [rsp], rdx
	mov qword ptr [rsp + 8], rsi
	mov qword ptr [rsp + 16], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, -1
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _exit
	# pop/peek {rdx, rsi, rdi}
	mov rdx, qword ptr [rsp]
	mov rsi, qword ptr [rsp + 8]
	mov rdi, qword ptr [rsp + 16]
	add rsp, 24
	jmp .L57
.L56:
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	# pop/peek {r12, r13}
	mov r12, qword ptr [rsp]
	mov r13, qword ptr [rsp + 8]
	add rsp, 16
	pop rbp
	ret
.L57:
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_findMoveWithGivenValue:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
	# push {rcx, rdx, rsi, rdi}
	sub rsp, 32
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	mov rax, rdx
	mov rdx, rax
	call wacc_findMoveWithGivenValueRow
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	add rsp, 32
	mov al, r11b
	mov cl, al
	cmp cl, 1
	je .L58
	# push {rcx, rdx, rsi, rdi}
	sub rsp, 32
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	mov rax, rdx
	mov rdx, rax
	call wacc_findMoveWithGivenValueRow
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	add rsp, 32
	mov al, r11b
	mov cl, al
	cmp cl, 1
	je .L60
	# push {rcx, rdx, rsi, rdi}
	sub rsp, 32
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r15
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	mov rax, rdx
	mov rdx, rax
	call wacc_findMoveWithGivenValueRow
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	add rsp, 32
	mov al, r11b
	mov cl, al
	cmp cl, 1
	je .L62
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
	jmp .L63
.L62:
	mov r10d, 0
	mov eax, 3
	mov r9, rdx
	call _arrStore4
.L63:
	jmp .L61
.L60:
	mov r10d, 0
	mov eax, 2
	mov r9, rdx
	call _arrStore4
.L61:
	jmp .L59
.L58:
	mov r10d, 0
	mov eax, 1
	mov r9, rdx
	call _arrStore4
.L59:
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

wacc_findMoveWithGivenValueRow:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
	# push {rcx, rdx, rsi, rdi}
	sub rsp, 32
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	call wacc_hasGivenStateValue
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	add rsp, 32
	mov al, r11b
	mov cl, al
	cmp cl, 1
	je .L64
	# push {rcx, rdx, rsi, rdi}
	sub rsp, 32
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	call wacc_hasGivenStateValue
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	add rsp, 32
	mov al, r11b
	mov cl, al
	cmp cl, 1
	je .L66
	# push {rcx, rdx, rsi, rdi}
	sub rsp, 32
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r15
	mov rdi, rax
	mov eax, esi
	mov esi, eax
	call wacc_hasGivenStateValue
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	add rsp, 32
	mov al, r11b
	mov cl, al
	cmp cl, 1
	je .L68
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
	jmp .L69
.L68:
	mov r10d, 1
	mov eax, 3
	mov r9, rdx
	call _arrStore4
.L69:
	jmp .L67
.L66:
	mov r10d, 1
	mov eax, 2
	mov r9, rdx
	call _arrStore4
.L67:
	jmp .L65
.L64:
	mov r10d, 1
	mov eax, 1
	mov r9, rdx
	call _arrStore4
.L65:
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

wacc_hasGivenStateValue:
	push rbp
	push r12
	mov rbp, rsp
	cmp rdi, 0
	je .L70
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov eax, eax
	mov r12d, eax
	cmp r12d, esi
	sete al
	push ax
	pop ax
	mov al, al
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	jmp .L71
.L70:
	mov al, 0
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
.L71:
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_notifyMoveAI:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rcx, 0
	je _errNull
	mov rax, qword ptr [rcx + 8]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r13, 0
	je _errNull
	mov rax, qword ptr [r13 + 8]
	mov rax, rax
	mov r14, rax
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
	lea rax, [rip + .L.str25]
	push rax
	pop rax
	mov rax, rax
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
	mov rax, r14
	mov rdi, rax
	mov eax, r8d
	mov esi, eax
	mov eax, r9d
	mov edx, eax
	call wacc_deleteAllOtherChildren
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	cmp rcx, 0
	je _errNull
	mov rax, r11
	mov qword ptr [rcx + 8], rax
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
	mov rax, r12
	mov rdi, rax
	call wacc_deleteThisStateOnly
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r15b, al
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

wacc_deleteAllOtherChildren:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	sub rsp, 9
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
	mov rax, 0
	mov rcx, rax
	mov rax, 0
	mov r8, rax
	mov rax, 0
	mov r9, rax
	cmp esi, 1
	je .L72
	mov rax, r13
	mov r8, rax
	cmp esi, 2
	je .L74
	mov rax, r15
	mov rcx, rax
	mov rax, r14
	mov r9, rax
	jmp .L75
.L74:
	mov rax, r14
	mov rcx, rax
	mov rax, r15
	mov r9, rax
.L75:
	jmp .L73
.L72:
	mov rax, r13
	mov rcx, rax
	mov rax, r14
	mov r8, rax
	mov rax, r15
	mov r9, rax
.L73:
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
	mov eax, edx
	mov esi, eax
	call wacc_deleteAllOtherChildrenRow
	mov r11, rax
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov rax, r11
	mov qword ptr [rbp - 9], rax
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
	call wacc_deleteChildrenStateRecursivelyRow
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov byte ptr [rbp - 1], al
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
	call wacc_deleteChildrenStateRecursivelyRow
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov byte ptr [rbp - 1], al
	mov rax, qword ptr [rbp - 9]
	mov rax, rax
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
	add rsp, 9
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_deleteAllOtherChildrenRow:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
	mov rax, 0
	mov rcx, rax
	mov rax, 0
	mov rdx, rax
	mov rax, 0
	mov r8, rax
	cmp esi, 1
	je .L76
	mov rax, r13
	mov rdx, rax
	cmp esi, 2
	je .L78
	mov rax, r15
	mov rcx, rax
	mov rax, r14
	mov r8, rax
	jmp .L79
.L78:
	mov rax, r14
	mov rcx, rax
	mov rax, r15
	mov r8, rax
.L79:
	jmp .L77
.L76:
	mov rax, r13
	mov rcx, rax
	mov rax, r14
	mov rdx, rax
	mov rax, r15
	mov r8, rax
.L77:
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
	mov rax, rdx
	mov rdi, rax
	call wacc_deleteStateTreeRecursively
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r9b, al
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
	call wacc_deleteStateTreeRecursively
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r9b, al
	mov rax, rcx
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

wacc_deleteStateTreeRecursively:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je .L80
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	call wacc_deleteChildrenStateRecursively
	mov r11b, al
	pop rdi
	mov al, r11b
	mov r15b, al
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	call wacc_deleteThisStateOnly
	mov r11b, al
	pop rdi
	mov al, r11b
	mov r15b, al
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
	jmp .L81
.L80:
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
.L81:
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_deleteThisStateOnly:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	call wacc_freeBoard
	mov r11b, al
	pop rdi
	mov al, r11b
	mov r15b, al
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	call wacc_freePointers
	mov r11b, al
	pop rdi
	mov al, r11b
	mov r15b, al
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _freepair
	pop rdi
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _freepair
	pop rdi
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

wacc_freePointers:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	call wacc_freePointersRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	call wacc_freePointersRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r15
	mov rdi, rax
	call wacc_freePointersRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _freepair
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _freepair
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
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

wacc_freePointersRow:
	push rbp
	push r12
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _freepair
	pop rdi
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _freepair
	pop rdi
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_deleteChildrenStateRecursively:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	call wacc_deleteChildrenStateRecursivelyRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	call wacc_deleteChildrenStateRecursivelyRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r15
	mov rdi, rax
	call wacc_deleteChildrenStateRecursivelyRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
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

wacc_deleteChildrenStateRecursivelyRow:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	call wacc_deleteStateTreeRecursively
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	call wacc_deleteStateTreeRecursively
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r15
	mov rdi, rax
	call wacc_deleteStateTreeRecursively
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
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

wacc_askForAMove:
	push rbp
	push r12
	mov rbp, rsp
	cmp sil, dl
	je .L82
	# push {rcx, rdx, rsi, rdi, r8}
	sub rsp, 40
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	mov al, sil
	mov sil, al
	mov al, dl
	mov dl, al
	mov rax, rcx
	mov rcx, rax
	mov rax, r8
	mov r8, rax
	call wacc_askForAMoveAI
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	add rsp, 40
	mov al, r11b
	mov r12b, al
	jmp .L83
.L82:
	# push {rcx, rdx, rsi, rdi, r8}
	sub rsp, 40
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	mov qword ptr [rsp + 32], r8
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	mov rax, r8
	mov rsi, rax
	call wacc_askForAMoveHuman
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	add rsp, 40
	mov al, r11b
	mov r12b, al
.L83:
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_placeMove:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	mov rax, 0
	mov r12, rax
	cmp edx, 2
	jle .L84
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	push rax
	pop rax
	mov rax, rax
	mov r12, rax
	jmp .L85
.L84:
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r13, rax
	cmp edx, 1
	je .L86
	cmp r13, 0
	je _errNull
	mov rax, qword ptr [r13 + 8]
	mov rax, rax
	push rax
	pop rax
	mov rax, rax
	mov r12, rax
	jmp .L87
.L86:
	cmp r13, 0
	je _errNull
	mov rax, qword ptr [r13]
	mov rax, rax
	push rax
	pop rax
	mov rax, rax
	mov r12, rax
.L87:
.L85:
	cmp ecx, 2
	jle .L88
	cmp r12, 0
	je _errNull
	mov al, sil
	mov qword ptr [r12 + 8], rax
	jmp .L89
.L88:
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp ecx, 1
	je .L90
	cmp r13, 0
	je _errNull
	mov al, sil
	mov qword ptr [r13 + 8], rax
	jmp .L91
.L90:
	cmp r13, 0
	je _errNull
	mov al, sil
	mov qword ptr [r13], rax
.L91:
.L89:
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

wacc_notifyMove:
	push rbp
	push r12
	mov rbp, rsp
	cmp sil, dl
	je .L92
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
	mov rax, rdi
	mov rdi, rax
	mov al, sil
	mov sil, al
	mov al, dl
	mov dl, al
	mov eax, r8d
	mov ecx, eax
	mov eax, r9d
	mov r8d, eax
	call wacc_notifyMoveHuman
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r12b, al
	jmp .L93
.L92:
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
	mov rax, rdi
	mov rdi, rax
	mov al, sil
	mov sil, al
	mov al, dl
	mov dl, al
	mov rax, rcx
	mov rcx, rax
	mov eax, r8d
	mov r8d, eax
	mov eax, r9d
	mov r9d, eax
	call wacc_notifyMoveAI
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r12b, al
.L93:
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_oppositeSymbol:
	push rbp
	mov rbp, rsp
	cmp dil, 120
	je .L94
	cmp dil, 111
	je .L96
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
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, -1
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _exit
	pop rdi
	jmp .L97
.L96:
	mov al, 120
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop rbp
	ret
.L97:
	jmp .L95
.L94:
	mov al, 111
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop rbp
	ret
.L95:
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_symbolAt:
	push rbp
	# push {r12, r13, r14}
	sub rsp, 24
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov rbp, rsp
	mov rax, 0
	mov r12, rax
	cmp esi, 2
	jle .L98
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	push rax
	pop rax
	mov rax, rax
	mov r12, rax
	jmp .L99
.L98:
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r14, rax
	cmp esi, 1
	je .L100
	cmp r14, 0
	je _errNull
	mov rax, qword ptr [r14 + 8]
	mov rax, rax
	push rax
	pop rax
	mov rax, rax
	mov r12, rax
	jmp .L101
.L100:
	cmp r14, 0
	je _errNull
	mov rax, qword ptr [r14]
	mov rax, rax
	push rax
	pop rax
	mov rax, rax
	mov r12, rax
.L101:
.L99:
	mov al, 0
	mov r13b, al
	cmp edx, 2
	jle .L102
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	push rax
	pop rax
	mov al, al
	mov r13b, al
	jmp .L103
.L102:
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r14, rax
	cmp edx, 1
	je .L104
	cmp r14, 0
	je _errNull
	mov rax, qword ptr [r14 + 8]
	mov rax, rax
	push rax
	pop rax
	mov al, al
	mov r13b, al
	jmp .L105
.L104:
	cmp r14, 0
	je _errNull
	mov rax, qword ptr [r14]
	mov rax, rax
	push rax
	pop rax
	mov al, al
	mov r13b, al
.L105:
.L103:
	mov al, r13b
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

wacc_containEmptyCell:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
	# push {rcx, rdx, rsi, rdi}
	sub rsp, 32
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	call wacc_containEmptyCellRow
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	add rsp, 32
	mov al, r11b
	mov cl, al
	# push {rcx, rdx, rsi, rdi}
	sub rsp, 32
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	call wacc_containEmptyCellRow
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	add rsp, 32
	mov al, r11b
	mov dl, al
	# push {rcx, rdx, rsi, rdi}
	sub rsp, 32
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdx
	mov qword ptr [rsp + 16], rsi
	mov qword ptr [rsp + 24], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r15
	mov rdi, rax
	call wacc_containEmptyCellRow
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	add rsp, 32
	mov al, r11b
	mov sil, al
	cmp cl, 1
	je .L106
	cmp dl, 1
	je .L107
	cmp sil, 1
.L107:
	sete al
	push ax
	pop ax
	cmp al, 1
.L106:
	sete al
	push ax
	pop ax
	mov al, al
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

wacc_containEmptyCellRow:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov al, al
	mov r13b, al
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov al, al
	mov r14b, al
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov al, al
	mov r15b, al
	cmp r13b, 0
	sete al
	push ax
	pop ax
	cmp al, 1
	je .L108
	cmp r14b, 0
	sete al
	push ax
	pop ax
	cmp al, 1
	je .L109
	cmp r15b, 0
	sete al
	push ax
	pop ax
	cmp al, 1
.L109:
	sete al
	push ax
	pop ax
	cmp al, 1
.L108:
	sete al
	push ax
	pop ax
	mov al, al
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

wacc_hasWon:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	sub rsp, 1
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
	mov rax, rdi
	mov rdi, rax
	mov eax, 1
	mov esi, eax
	mov eax, 1
	mov edx, eax
	call wacc_symbolAt
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r12b, al
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
	mov rax, rdi
	mov rdi, rax
	mov eax, 1
	mov esi, eax
	mov eax, 2
	mov edx, eax
	call wacc_symbolAt
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r13b, al
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
	mov rax, rdi
	mov rdi, rax
	mov eax, 1
	mov esi, eax
	mov eax, 3
	mov edx, eax
	call wacc_symbolAt
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r14b, al
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
	mov rax, rdi
	mov rdi, rax
	mov eax, 2
	mov esi, eax
	mov eax, 1
	mov edx, eax
	call wacc_symbolAt
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r15b, al
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
	mov rax, rdi
	mov rdi, rax
	mov eax, 2
	mov esi, eax
	mov eax, 2
	mov edx, eax
	call wacc_symbolAt
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov cl, al
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
	mov rax, rdi
	mov rdi, rax
	mov eax, 2
	mov esi, eax
	mov eax, 3
	mov edx, eax
	call wacc_symbolAt
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov dl, al
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
	mov rax, rdi
	mov rdi, rax
	mov eax, 3
	mov esi, eax
	mov eax, 1
	mov edx, eax
	call wacc_symbolAt
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r8b, al
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
	mov rax, rdi
	mov rdi, rax
	mov eax, 3
	mov esi, eax
	mov eax, 2
	mov edx, eax
	call wacc_symbolAt
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov r9b, al
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
	mov rax, rdi
	mov rdi, rax
	mov eax, 3
	mov esi, eax
	mov eax, 3
	mov edx, eax
	call wacc_symbolAt
	mov r11b, al
	# pop/peek {rcx, rdx, rsi, rdi, r8, r9}
	mov rcx, qword ptr [rsp]
	mov rdx, qword ptr [rsp + 8]
	mov rsi, qword ptr [rsp + 16]
	mov rdi, qword ptr [rsp + 24]
	mov r8, qword ptr [rsp + 32]
	mov r9, qword ptr [rsp + 40]
	add rsp, 48
	mov al, r11b
	mov byte ptr [rbp - 1], al
	cmp r12b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L110
	cmp r13b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L111
	cmp r14b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
.L111:
	sete al
	push ax
	pop ax
	cmp al, 1
.L110:
	sete al
	push ax
	pop ax
	cmp al, 1
	je .L112
	cmp r15b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L113
	cmp cl, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L114
	cmp dl, sil
	sete al
	push ax
	pop ax
	cmp al, 1
.L114:
	sete al
	push ax
	pop ax
	cmp al, 1
.L113:
	sete al
	push ax
	pop ax
	cmp al, 1
	je .L115
	cmp r8b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L116
	cmp r9b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L117
	mov al, byte ptr [rbp - 1]
	cmp al, sil
	sete al
	push ax
	pop ax
	cmp al, 1
.L117:
	sete al
	push ax
	pop ax
	cmp al, 1
.L116:
	sete al
	push ax
	pop ax
	cmp al, 1
	je .L118
	cmp r12b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L119
	cmp r15b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L120
	cmp r8b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
.L120:
	sete al
	push ax
	pop ax
	cmp al, 1
.L119:
	sete al
	push ax
	pop ax
	cmp al, 1
	je .L121
	cmp r13b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L122
	cmp cl, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L123
	cmp r9b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
.L123:
	sete al
	push ax
	pop ax
	cmp al, 1
.L122:
	sete al
	push ax
	pop ax
	cmp al, 1
	je .L124
	cmp r14b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L125
	cmp dl, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L126
	mov al, byte ptr [rbp - 1]
	cmp al, sil
	sete al
	push ax
	pop ax
	cmp al, 1
.L126:
	sete al
	push ax
	pop ax
	cmp al, 1
.L125:
	sete al
	push ax
	pop ax
	cmp al, 1
	je .L127
	cmp r12b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L128
	cmp cl, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L129
	mov al, byte ptr [rbp - 1]
	cmp al, sil
	sete al
	push ax
	pop ax
	cmp al, 1
.L129:
	sete al
	push ax
	pop ax
	cmp al, 1
.L128:
	sete al
	push ax
	pop ax
	cmp al, 1
	je .L130
	cmp r14b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L131
	cmp cl, sil
	sete al
	push ax
	pop ax
	cmp al, 1
	jne .L132
	cmp r8b, sil
	sete al
	push ax
	pop ax
	cmp al, 1
.L132:
	sete al
	push ax
	pop ax
	cmp al, 1
.L131:
	sete al
	push ax
	pop ax
	cmp al, 1
.L130:
	sete al
	push ax
	pop ax
	cmp al, 1
.L127:
	sete al
	push ax
	pop ax
	cmp al, 1
.L124:
	sete al
	push ax
	pop ax
	cmp al, 1
.L121:
	sete al
	push ax
	pop ax
	cmp al, 1
.L118:
	sete al
	push ax
	pop ax
	cmp al, 1
.L115:
	sete al
	push ax
	pop ax
	cmp al, 1
.L112:
	sete al
	push ax
	pop ax
	mov al, al
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
	add rsp, 1
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_allocateNewBoard:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	call wacc_allocateNewRow
	mov r11, rax
	pop rcx
	mov rax, r11
	mov r12, rax
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	call wacc_allocateNewRow
	mov r11, rax
	pop rcx
	mov rax, r11
	mov r13, rax
	push rcx
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	call wacc_allocateNewRow
	mov r11, rax
	pop rcx
	mov rax, r11
	mov r14, rax
	push rcx
	mov edi, 16
	call _malloc
	mov r11, rax
	pop rcx
	mov rax, r12
	mov qword ptr [r11], rax
	mov rax, r13
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r15, rax
	push rcx
	mov edi, 16
	call _malloc
	mov r11, rax
	pop rcx
	mov rax, r15
	mov qword ptr [r11], rax
	mov rax, r14
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov rcx, rax
	mov rax, rcx
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

wacc_allocateNewRow:
	push rbp
	# push {r12, r13}
	sub rsp, 16
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov rbp, rsp
	mov edi, 16
	call _malloc
	mov r11, rax
	mov al, 0
	mov qword ptr [r11], rax
	mov al, 0
	mov qword ptr [r11 + 8], rax
	mov rax, r11
	mov r12, rax
	mov edi, 16
	call _malloc
	mov r11, rax
	mov rax, r12
	mov qword ptr [r11], rax
	mov al, 0
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

wacc_freeBoard:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	call wacc_freeRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	call wacc_freeRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r15
	mov rdi, rax
	call wacc_freeRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _freepair
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _freepair
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
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

wacc_freeRow:
	push rbp
	push r12
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r12
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _freepair
	pop rdi
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, rdi
	mov rdi, rax
	# statement primitives do not return results (but will clobber r0/rax)
	call _freepair
	pop rdi
	mov al, 1
	# reset the stack pointer, undoing any pushes: this is often unnecessary, but is cheap
	mov rsp, rbp
	pop r12
	pop rbp
	ret
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_printAiData:
	push rbp
	# push {r12, r13, r14}
	sub rsp, 24
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r13, rax
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	call wacc_printStateTreeRecursively
	mov r11b, al
	pop rdi
	mov al, r11b
	mov r14b, al
	push rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, 0
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _exit
	pop rdi
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_printStateTreeRecursively:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je .L133
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov eax, eax
	mov r15d, eax
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, 118
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, 61
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov eax, r15d
	mov edi, eax
	# statement primitives do not return results (but will clobber r0/rax)
	call _printi
	call _println
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	call wacc_printBoard
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	call wacc_printChildrenStateTree
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov al, 112
	mov dil, al
	# statement primitives do not return results (but will clobber r0/rax)
	call _printc
	call _println
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
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
	jmp .L134
.L133:
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
.L134:
	# 'ere be dragons: this is 100% dead code, functions always end in returns!

wacc_printChildrenStateTree:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	call wacc_printChildrenStateTreeRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	call wacc_printChildrenStateTreeRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r15
	mov rdi, rax
	call wacc_printChildrenStateTreeRow
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
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

wacc_printChildrenStateTreeRow:
	push rbp
	# push {r12, r13, r14, r15}
	sub rsp, 32
	mov qword ptr [rsp], r12
	mov qword ptr [rsp + 8], r13
	mov qword ptr [rsp + 16], r14
	mov qword ptr [rsp + 24], r15
	mov rbp, rsp
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi]
	mov rax, rax
	mov r12, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12]
	mov rax, rax
	mov r13, rax
	cmp r12, 0
	je _errNull
	mov rax, qword ptr [r12 + 8]
	mov rax, rax
	mov r14, rax
	cmp rdi, 0
	je _errNull
	mov rax, qword ptr [rdi + 8]
	mov rax, rax
	mov r15, rax
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r13
	mov rdi, rax
	call wacc_printStateTreeRecursively
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r14
	mov rdi, rax
	call wacc_printStateTreeRecursively
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
	# push {rcx, rdi}
	sub rsp, 16
	mov qword ptr [rsp], rcx
	mov qword ptr [rsp + 8], rdi
	# Set up R11 as a temporary second base pointer for the caller saved things
	mov r11, rsp
	mov rax, r15
	mov rdi, rax
	call wacc_printStateTreeRecursively
	mov r11b, al
	# pop/peek {rcx, rdi}
	mov rcx, qword ptr [rsp]
	mov rdi, qword ptr [rsp + 8]
	add rsp, 16
	mov al, r11b
	mov cl, al
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
