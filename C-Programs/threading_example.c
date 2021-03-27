#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>


void* isEven(void* num) {

  int* res;

  res = malloc(sizeof(int));

  if( *((int*)num) % 2 == 0)
    *res = 1;
  else
    *res = 0;

  pthread_exit(res);

}


int main(int argc, char* argv[]) {

  pthread_t thread;

  int* arg;

  arg = (int*) malloc(sizeof(int));

  *arg = 2;

  int* ret;

  pthread_create(&thread, NULL, isEven, arg);

  pthread_join(thread, (void**) &ret);

  printf("The argument [%d] to isEven was %s\n", *arg, (*ret == 0 ? "odd. How odd is that?" : "even. My goodness.")); 



  return 0;
} 