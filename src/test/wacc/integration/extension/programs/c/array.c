#include <stdio.h>
#include <stdlib.h>

int main() {
    // Allocate memory for 2 integers
    int *arr = (int *)malloc(2 * sizeof(int));
    arr[0] = 1;
    arr[1] = 2;

    return 0;
}
