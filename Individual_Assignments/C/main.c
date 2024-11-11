#include<stdio.h>
#include<stdlib.h>
#include<mach/mach_time.h>

// Matrix multiplication function
void matrix_multiply(register int size, double **matrix1, double **matrix2, double **result) {
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            result[i][j] = 0.0;
            for (register int k = 0; k < size; k++)
                result[i][j] += matrix1[i][k] * matrix2[k][j];
        }
    }
}

// Function to generate two random matrices
void generate_matrices(int size, double **matrix1, double **matrix2) {
    for (int i = 0; i < size; i++)
        for (int j = 0; j < size; j++) {
            matrix1[i][j] = rand() / (double)RAND_MAX;
            matrix2[i][j] = rand() / (double)RAND_MAX;
        }
}

// Function to get current time in nanoseconds
uint64_t get_time(void) {
    static mach_timebase_info_data_t info = {0,0};

    if (info.denom == 0) {
        mach_timebase_info(&info);
    }

    uint64_t time = mach_absolute_time();
    time *= info.numer;
    time /= info.denom;

    return time;
}

// Function to compare two uint64_t values
int compare_uint64(const void *a, const void *b) {
    uint64_t va = *(uint64_t*)a;
    uint64_t vb = *(uint64_t*)b;
    if (va > vb)
        return 1;
    else if (va < vb)
        return -1;
    else
        return 0;
}

int main() {
    int sizes[] = {1, 5, 10, 50, 100, 150, 200, 500, 1000, 1500};
    int iterations = 2, rounds = 2;
    int total_count = iterations * rounds;

    uint64_t *times = malloc(sizeof(uint64_t) * total_count);

    // Loop over matrix sizes
    for (int s = 0; s < 10; s++) {
        int size = sizes[s];
        double **matrix1 = malloc(size * sizeof(double *));
        double **matrix2 = malloc(size * sizeof(double *));
        double **result = malloc(size * sizeof(double *));
        for (int i = 0; i < size; i++) {
            matrix1[i] = malloc(size * sizeof(double));
            matrix2[i] = malloc(size * sizeof(double));
            result[i] = malloc(size * sizeof(double));
        }
        generate_matrices(size, matrix1, matrix2);

        // Do the benchmark
        for (int round = 0; round < rounds; round++) {
            for (int iteration = 0; iteration < iterations; iteration++) {
                uint64_t start = get_time();
                matrix_multiply(size, matrix1, matrix2, result);
                uint64_t end = get_time();
                times[round * iterations + iteration] = end - start;
            }
        }

        // Calculate statistics
        uint64_t total = 0;
        uint64_t min_time = times[0];
        uint64_t max_time = times[0];
        for (int i = 0; i < total_count; i++) {
            total += times[i];
            if (times[i] < min_time) min_time = times[i];
            if (times[i] > max_time) max_time = times[i];
        }
        uint64_t average_time = total / total_count;

        // Calculate median
        qsort(times, total_count, sizeof(uint64_t), compare_uint64);
        uint64_t median_time = (total_count % 2 == 0) ?
            (times[total_count/2] + times[total_count/2 - 1]) / 2
            : times[total_count/2];

        printf("Size: %d, Min time: %llu ns, Max time: %llu ns, Avg time: %llu ns, Median time: %llu ns\n",
               size, min_time, max_time, average_time, median_time);

        for (int i = 0; i < size; i++) {
            free(matrix1[i]);
            free(matrix2[i]);
            free(result[i]);
        }
        free(matrix1);
        free(matrix2);
        free(result);
    }

    free(times);

    return 0;
}