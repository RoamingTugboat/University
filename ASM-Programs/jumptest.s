# Dieses Programm testet, welcher von drei Spr�ngen in ihm
# ausgef�hrt wurde. Gibt entsprechend 1, 2 oder 3 zur�ck.

.section .text
.globl _start

_start:
 movl $5, %eax
 cmpl $0, %eax
 je exit1

 movl $0, %eax
 cmpl $-1, %eax
 jl exit2

 jg cont
 jmp exitDefault

cont:
 movl $5, %eax
 cmpl $5, %eax
 jge exit3

exitDefault:
 movl $1, %eax
 movl $0, %ebx
 int $0x80

 
 
exit1:
 movl $1, %eax
 movl $1, %ebx
 int $0x80
 
exit2:
 movl $1, %eax
 movl $2, %ebx
 int $0x80
 
exit3:
 movl $1, %eax
 movl $3, %ebx
 int $0x80