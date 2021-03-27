/* (C) 2009 Gundolf Kiefer, Augsburg University of Applied Sciences */

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>

#define THREADS 8
#define ITERATIONS 50000 // decreased by 99% to compensate for amount of threads and how threads simulate resource usage in person_auf_toilette()

#define LEER 0
#define FRAU 1
#define MANN 2



pthread_mutex_t mutex_m, mutex_f, mutex_access;

volatile int anzeige = LEER;

volatile int frauen = 0;  //concurrent type1 accessing of resource
volatile int maenner = 0; //concurrent type2 accessing of resource

volatile int leerTotal = 0;    //counts empty states (resource not being used by any thread)
volatile int frauenTotal = 0;  //keeps track of total type1 accesses
volatile int maennerTotal = 0; //keeps track of total type1 accesses



void frau_moechte_eintreten (int threadID) {
    pthread_mutex_lock(&mutex_f);
    frauen++;
    frauenTotal++;
    if(frauen==1) {
      pthread_mutex_lock(&mutex_access);
      anzeige = FRAU;
    }
    //printf("%i| IN  FRAU, %i |ANZ=%i ... \n", threadID, frauen, anzeige);
    pthread_mutex_unlock(&mutex_f);
}

void mann_moechte_eintreten (int threadID) {
    pthread_mutex_lock(&mutex_m);
    maenner++;
    maennerTotal++;
    if(maenner==1) {
        pthread_mutex_lock(&mutex_access);
        anzeige = MANN;
    }
    //printf("%i| IN  MANN, %i |ANZ=%i ... \n", threadID, maenner, anzeige);
    pthread_mutex_unlock(&mutex_m);
}


void frau_verlaesst_die_toilette (int threadID) {
  pthread_mutex_lock(&mutex_f);
  frauen--;
  if(frauen < 1) {  
    leerTotal++;
    anzeige = LEER;
    pthread_mutex_unlock(&mutex_access);
  }
  //printf("%i| OUT FRAU, %i |ANZ=%i\n", threadID, frauen, anzeige);
  pthread_mutex_unlock(&mutex_f);
}

void mann_verlaesst_die_toilette (int threadID) {
  pthread_mutex_lock(&mutex_m);
  maenner--;
  if(maenner < 1) {  
    leerTotal++;
    anzeige = LEER;
    pthread_mutex_unlock(&mutex_access);
  }
  //printf("%i| OUT MANN, %i |ANZ=%i\n", threadID, maenner, anzeige);
  pthread_mutex_unlock(&mutex_m);
}



/* This method idles for a short moment, simulating resource usage */
void person_auf_toilette(int threadID) {
  int i;
  int max = (rand()%256 + 512);
  for(i=0; i<max; i++) {
     int* ip = (int*) malloc(sizeof(int));
     free(ip);
  }
  //printf("%i| DONE\n", threadID);
}



void *thread_routine_rand(void* data) {

  int threadID = *(int*)data;
  int n;

  for (n=0; n<ITERATIONS/THREADS; n++) {

      if ( rand() & 1 ) {
        frau_moechte_eintreten(threadID);
        person_auf_toilette(threadID);
        if(anzeige != FRAU) {
          printf("%i   ERROR > FRAU LEAVING, BUT ANZEIGE WAS != FRAU\n", threadID);
          exit(2);
        }
        frau_verlaesst_die_toilette(threadID);
      } else {
        mann_moechte_eintreten(threadID);
        person_auf_toilette(threadID);
        if(anzeige != MANN) {
          printf("%i   ERROR > FRAU LEAVING, BUT ANZEIGE WAS != MANN\n", threadID);
          exit(2);
        }
        mann_verlaesst_die_toilette(threadID);
      }
    
  }

  return NULL;
}



int main() {

  pthread_t threads[THREADS];
  int threadID[THREADS];
  int n;

  pthread_mutex_init(&mutex_m, 0);
  pthread_mutex_init(&mutex_f, 0);
  pthread_mutex_init(&mutex_access, 0);


  puts(" ");
  puts(" ");
  puts("This program simulates concurrent access of a resource by two different");
  puts("kinds of methods. While the resource is being accessed by one kind of method,");
  puts("access through the other kind will be impossible.");
  puts(" ");
  printf("  Creating %i threads...\n", THREADS);

  for (n = 0; n < THREADS; n++) {
    threadID[n] = n;
    if (pthread_create(&threads[n], NULL, thread_routine_rand, (void*)&(threadID[n])) != 0) {
      puts("  Encountered an error during thread creation.");
      exit(3);
    }
  }

  puts("  Threads created successfully. Running simulation...");

  for (n = 0; n < THREADS; n++) {
    pthread_join(threads[n], NULL);
  }

  puts("");
  printf("  Simulation completed after %i successful accesses.\n\n", ITERATIONS);
  printf("    Resource occupation by type 1 methods:  %i\n    Resource occupation by type 2 methods:  %i\n\n", maennerTotal, frauenTotal);
  double percentageEmpties = ((double)leerTotal/ITERATIONS)*100;
  printf("  %.2f%% of all access attempts resulted in a state change.\n", 100-percentageEmpties);  puts(" ");   puts("------------------ ");


  pthread_mutex_destroy(&mutex_m);
  pthread_mutex_destroy(&mutex_f);
  pthread_mutex_destroy(&mutex_access);

  return 0;

}