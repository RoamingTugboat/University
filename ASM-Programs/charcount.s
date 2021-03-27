# PURPOSE:
# This Program prints out the amount of symbols and line
# breaks in a given text file.
#
# USAGE:
# First parameter will be used as file name. Result will be
# printed in console.

.section .data
  AMOUNT_SYMBOLS: .long  0
  AMOUNT_BREAKS:  .long  0

  s_CountedSymbols: .asciz "Counted Symbols: %d\n"
  s_CountedBreaks:  .asciz "Counted Breaks: %d\n"
  
.section .bss
  .equ   BUFFER_SIZE, 500
  .lcomm BUFFER_SPACE, BUFFER_SIZE
  .lcomm FD_SOURCE_FILE, 32

.section .text
  .globl _start

_start:

 movl %esp, %ebp 
 
 #Open given file.
 movl $5, %eax
 movl 8(%ebp), %ebx  #use first parameter as file name
 movl $0, %ecx
 movl $0666, %edx
 int  $0x80
 
 movl %eax, FD_SOURCE_FILE
 
#Iterate over buffer:
#Read from given file:
read_loop_begin:

 movl $3, %eax
 movl FD_SOURCE_FILE, %ebx 
 movl $BUFFER_SPACE, %ecx  
 movl $BUFFER_SIZE, %edx
 int  $0x80

 cmpl $0, %eax  #if end of file is reached
 je end_loop    #... jump to end_loop
 
 pushl $BUFFER_SPACE #BUFFER MEM ADDRESS
 pushl %eax          #BUFFER LENGTH
 call  count_stuff         
 addl  $8, %esp

 jmp read_loop_begin


#Write result to STDOUT
end_loop:
 pushl AMOUNT_SYMBOLS
 pushl $s_CountedSymbols
 call  printf
 addl $8, %esp

 pushl AMOUNT_BREAKS
 pushl $s_CountedBreaks
 call  printf
 addl $8, %esp



#Close input file
 movl $6, %eax
 movl FD_SOURCE_FILE, %ebx
 int  $0x80

#Exit
 movl $1, %eax
 movl $0, %ebx
 int  $0x80


######################
# READ THE BUFFER
######################
#If there's a symbol, inc numOfSymbols.
#If there's a '\n', inc numOfLines.
######################

.type count_stuff, @function
count_stuff:
 pushl %ebp
 movl  %esp, %ebp

 movl 12(%ebp), %eax #Buffer 
 movl  8(%ebp), %ebx #Buffer length
 movl $0, %edi

 cmpl $0, %ebx   #If buffer length was 0,
 je stop_counting #Jump out before looping

count_loop:
 movb (%eax, %edi, 1), %cl
 cmpb $10, %cl    #10 is ASCII line break
 je increment_breaks
 
 cmpb $33, %cl    #visible ASCII symbols range from 33-126
 jl continue_counting
 cmpb $126, %cl
 jg continue_counting

 jmp increment_symbols


increment_breaks:
 incl AMOUNT_BREAKS
 jmp continue_counting

increment_symbols:
 incl AMOUNT_SYMBOLS
 jmp continue_counting


continue_counting:
 incl %edi
 cmpl %edi, %ebx
 jne count_loop    #if end of buffer is NOT reached, jump back into loop.

stop_counting:
 movl %ebp, %esp   
 popl %ebp
 ret