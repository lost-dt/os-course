#include <iostream>
#include "Allocator.h"

using namespace std;

int main() {
    size_t size = 1024 * 4096;
    size_t ps = 4096;
    int n = 300;
    Allocator al(size, ps);
    auto **addrArray = new size_t *[n];
    cout << "start!" << endl;
    for (int i = 0; i < n; i++) {
        addrArray[i] = (size_t *) al.mem_alloc(rand());
    }
    al.mem_dump();
    for (int i = 0; i < n / 3; i++) {
        al.mem_realloc(addrArray[i], rand());
    }

    al.mem_dump();
    for (int i = n / 2; i < n; i++) {
        al.mem_free(addrArray[i]);
    }
    al.mem_dump();
    cout << "finish!" << endl;
    getchar();
    return 0;
}
