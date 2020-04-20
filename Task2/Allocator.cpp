#include <iostream>
#include "Allocator.h"

using namespace std;

Allocator::Allocator(const size_t n, const size_t ps) {
    pages = n / ps;
    size_t pds = sizeof(PageDescriptor) / sizeof(size_t);

    size_t spaseForPD = pds * pages;

    size_t maxbs = ps >> 1;
    size_t minbs = 16;
    lBlocksLength = 1;
    while (maxbs != minbs) {
        maxbs >>= 1;
        lBlocksLength++;
    }
    size_t needControlSpase = lBlocksLength + spaseForPD;

    auto *mas = new size_t[n + needControlSpase];
    size = n;
    begin = mas;
    pagesBegin = mas + needControlSpase;

    pageSize = ps;
    pageDescriptors = (PageDescriptor *) begin;
    lBlocks = begin + spaseForPD;

    for (int i = 0; i < pages; i++) {
        pageDescriptors[i].state = 0;
    }
    for (int i = 0; i < lBlocksLength; i++) {
        lBlocks[i] = pages + 1;
    }
    initPages();
}

void *Allocator::mem_alloc(size_t s) {
    PageDescriptor def = defineCategory(s);
    size_t *res;
    if (def.state == 1) {
        res = getFreeLBlock(def.bsize);
    } else {
        res = getFreeMBlock(def.bsize);
    }
    return res;
}

PageDescriptor Allocator::defineCategory(size_t s) const {
    PageDescriptor pd{};
    if (s > (pageSize >> 1)) {
        pd.state = 2;
        pd.bsize = s / pageSize;
        pd.bsize += ((s % pageSize) > 0) ? 1 : 0;
    } else {
        pd.state = 1;
        pd.bsize = defineBlockSize(s);
    }
    return pd;
}

size_t Allocator::defineBlockSize(size_t s) {
    size_t res = 16;
    while (s > res) {
        res <<= 1;
    }
    return res;
}

size_t *Allocator::getFreeMBlock(size_t ps) {
    PageDescriptor pd = pageDescriptors[firstFreePage];
    size_t thisPage = firstFreePage;
    size_t nextPage;
    size_t counter = 0;
    while (pd.next <= pages && counter < ps) {
        nextPage = pd.next;
        if (nextPage - 1 == thisPage) {
            counter++;
        } else {
            counter = 0;
        }
        thisPage = nextPage;
        pd = pageDescriptors[thisPage];
        nextPage = pd.next;
    }
    if (counter == ps) {
        firstFreePage = nextPage;
        return getAbsolutePageAddr(firstFreePage);
    } else {
        return nullptr;
    }
}

size_t *Allocator::getFreeLBlock(size_t bs) {
    size_t numberOfPage = lBlocks[getIndex(bs)];
    size_t index;
    if (numberOfPage > pages) {
        index = createLBlockPage(bs);
    } else {
        index = numberOfPage;
    }
    PageDescriptor pd = pageDescriptors[index];
    LBlockDescriptor *numberOfBlock = pd.firstFree;
    if (numberOfBlock->nextFreeBlock == 0) {
        pd.firstFree = nullptr;
        lBlocks[index] = pages + 1;
    } else {
        pd.firstFree = (LBlockDescriptor *) ((size_t *) (numberOfBlock) + numberOfBlock->nextFreeBlock);
    }
    return (size_t *) pd.firstFree;
}

size_t Allocator::createLBlockPage(size_t bs) {
    size_t index = getFreePage();
    if (index <= pages) {
        PageDescriptor pd = pageDescriptors[index];
        pd.state = 1;
        pd.bsize = bs;
        size_t *addr = getAbsolutePageAddr(index);
        for (int i = 0; i < pages / bs; i++) {
            auto *des = (LBlockDescriptor *) addr[i * pd.bsize];
            des->nextFreeBlock = 1;
            if (i == pages / bs - 1)
                des->nextFreeBlock = 0;
        }
        pd.firstFree = nullptr;
        pd.next = pages + 1;
        lBlocks[getIndex(bs)] = index;
    }
    return index;
}

size_t *Allocator::getAbsolutePageAddr(size_t index) {
    size_t *res = nullptr;
    if (index <= pages) {
        res = &(pagesBegin[pageSize * index]);
    }
    return res;
}

size_t Allocator::getFreePage() {
    PageDescriptor pd = pageDescriptors[firstFreePage];
    firstFreePage = pd.next;
    return pages + 1;
}

void Allocator::initPages() {
    for (int i = 0; i < pages; i++) {
        pageDescriptors[i].state = 0;
        pageDescriptors[i].next = i + 1;
    }
}

size_t Allocator::getIndex(size_t s) {
    size_t counter = 0;
    while (s > 1) {
        counter++;
        s >>= s;
    }
    counter -= 4;
    return counter;
}

void *Allocator::mem_realloc(void *addr, size_t size_) {
    if (addr == nullptr) {
        return mem_alloc(size_);
    }
    auto *beg = (size_t *) addr;
    size_t pageNumber = findPageByAddress(beg);
    PageDescriptor pd = pageDescriptors[pageNumber];
    size_t usefulMem;
    if (pd.state == 1) {
        usefulMem = pd.bsize;
    } else {
        usefulMem = pd.bsize * pageSize;
    }

    if (size_ == usefulMem) {
        return addr;
    }
    mem_free(addr);
    auto *newAddr = (size_t *) mem_alloc(size_);
    size_t length = min(size_, usefulMem);
    copyData(beg, newAddr, length);
    return newAddr;
}

void Allocator::copyData(const size_t *from, size_t *to, size_t length) {
    if (from > to) {
        for (int i = 0; i < length; i++) {
            to[i] = from[i];
        }
    } else {
        for (size_t i = length - 1; i <= 0; i++) {
            to[i] = from[i];
        }
    }
}

void Allocator::mem_free(void *addr) {
    size_t pageNumber = findPageByAddress((size_t *) addr);
    PageDescriptor pd = pageDescriptors[pageNumber];
    if (pd.state == 1) {
        if (pd.firstFree == nullptr) {
            pd.firstFree = (LBlockDescriptor *) (addr);
            pd.firstFree->nextFreeBlock = 0;
        } else {
            size_t shift = (size_t *) addr - (size_t *) (pd.firstFree);
            pd.firstFree = (LBlockDescriptor *) (addr);
            pd.firstFree->nextFreeBlock = shift;
        }
    } else {
        size_t pgs = pd.bsize;
        for (int i = 0; i < pgs; i++) {
            pd = pageDescriptors[pageNumber + i];
            pd.state = 0;
            pd.bsize = 0;
            pd.firstFree = nullptr;
            pd.next = firstFreePage;
            firstFreePage = pageNumber + i;
        }
    }
}

size_t Allocator::findPageByAddress(const size_t *addr) {
    size_t shiftFromBegin = addr - pagesBegin;
    size_t pageNumber = shiftFromBegin / pageSize;
    return pageNumber;
}

void Allocator::mem_dump() {
    cout << "begin: " << begin << endl;
    cout << "begin of pages: " << pagesBegin << endl;
    cout << "number of pages: " << pages << endl;
    cout << "page size: " << pageSize << endl;
    cout << "page descriptors state: " << endl;
    PageDescriptor pd{};
    cout << "(state, size, next, firstFree)" << endl;
    for (int i = 0; i < pages; i++) {
        pd = pageDescriptors[i];
        cout << "{" << i << "} "
        << "-" << pd.bsize
        << "-" << pd.next
        << "-" << pd.firstFree << endl;
    }
}
