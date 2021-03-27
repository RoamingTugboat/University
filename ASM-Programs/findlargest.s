# Dieses Programm returnt den hoechsten Wert aus einer Liste von longs.

.section .data

  list:   .long 12,55,200,6,95,111,128,125,85,0
  list2:  .long 123,51,16,87,83,7,0
  list3:  .long 71,69,19,62,9,23,12,90,44,0


.section .text
  .globl _start


_start:
  pushl list        # call function with list1 as param
  call maximumfinder
  addl $4, %esp     # reset stack pointer to top of old stack
  
  
  pushl list2       #call function again, but with list2 
  call maximumfinder
  addl $4, %esp     # reset stack pointer to top of old stack
  
  
  pushl list3       #call function for the last time with list3
  call maximumfinder
  addl $4, %esp     # reset stack pointer to top of old stack
  
  
  movl %eax, %ebx    # copy result (returned in %eax) to ebx
  movl $1, %eax      # set exit code to 1
  int  $0x80         # exit program


# Purpose: Iterate over list until value '0' is reached.
#          Remember the largest value in the list.

.type maximumfinder, @function

maximumfinder:

  pushl %ebp
  movl  %esp, %ebp
  addl  $4, %esp        # local variable: largest list item
  movl  $0, %ecx        # counter = 0


   list_iterator_start:
  movl list(,%ecx,4), %eax  #copy current list item to eax
  incl %ecx           	  # increment counter

  cmpl $0, %eax        # is current list item = 0?
  je   loop_end        # then exit
  
  cmpl -4(%ebp), %eax   # current list item > old record?
  jg   replace_current  # then replace old record

  jmp list_iterator_start



   replace_current:
  movl %eax, -4(%ebp)     # move current list item into
  jmp list_iterator_start # local variable, then iterate again
  


   loop_end:
  movl -4(%ebp), %eax   # move return value to %eax
  movl %ebp, %esp       # 'delete' local variables
  popl %ebp             # reset ebp to old address
  ret                   # return eip to caller