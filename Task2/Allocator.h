#pragma once
#ifndef HELLO_ALLOCATOR_H
#define HELLO_ALLOCATOR_H

struct LBlockDescriptor {
    size_t nextFreeBlock;
};

struct PageDescriptor {
    size_t next;
    LBlockDescriptor *firstFree;
    size_t bsize;
    char state;
};

class Allocator {
public:
    Allocator(size_t ms, size_t ps);
    void *mem_alloc(size_t size);
    void *mem_realloc(void *addr, size_t size_);
    void mem_free(void *addr);
    void mem_dump();

private:
    size_t *begin;
    size_t *pagesBegin;
    size_t size;
    size_t pages;
    size_t pageSize;
    PageDescriptor *pageDescriptors;
    size_t *lBlocks;
    size_t lBlocksLength;
    size_t firstFreePage{};
    PageDescriptor defineCategory(size_t s) const;
    static size_t defineBlockSize(size_t s);
    void initPages();
    size_t *getFreeLBlock(size_t bs);
    static size_t getIndex(size_t s);
    size_t createLBlockPage(size_t bs);
    size_t getFreePage();
    size_t *getAbsolutePageAddr(size_t index);
    size_t *getFreeMBlock(size_t ps);
    size_t findPageByAddress(const size_t *addr);
    static void copyData(const size_t *from, size_t *to, size_t length);
};


#endif //HELLO_ALLOCATOR_H
