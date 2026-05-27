import { defineStore } from 'pinia'
import { ref } from 'vue'
import { bookAPI } from '../api'

export const useBookStore = defineStore('book', () => {
  const books = ref([])
  const loading = ref(false)
  const error = ref(null)

  const fetchBooks = async () => {
    loading.value = true
    try {
      const response = await bookAPI.getAll()
      books.value = response.data.data || []
      error.value = null
    } catch (e) {
      error.value = '获取图书列表失败'
      console.error(error.value, e)
    } finally {
      loading.value = false
    }
  }

  const addBook = async (bookData) => {
    try {
      const response = await bookAPI.create(bookData)
      books.value.push(response.data.data)
      return response.data.data
    } catch (e) {
      error.value = '创建图书失败'
      throw e
    }
  }

  const updateBook = async (id, bookData) => {
    try {
      const response = await bookAPI.update(id, bookData)
      const index = books.value.findIndex(b => b.id === id)
      if (index > -1) {
        books.value[index] = response.data.data
      }
      return response.data.data
    } catch (e) {
      error.value = '更新图书失败'
      throw e
    }
  }

  const deleteBook = async (id) => {
    try {
      await bookAPI.delete(id)
      books.value = books.value.filter(b => b.id !== id)
    } catch (e) {
      error.value = '删除图书失败'
      throw e
    }
  }

  const searchByTitle = async (title) => {
    try {
      const response = await bookAPI.searchByTitle(title)
      return response.data.data || []
    } catch (e) {
      error.value = '搜索失败'
      throw e
    }
  }

  const searchByCategory = async (category) => {
    loading.value = true
    try {
      const response = await bookAPI.searchByCategory(category)
      books.value = response.data.data || []
      error.value = null
      return books.value
    } catch (e) {
      error.value = '按分类搜索失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  // 获取所有分类
  const getCategories = () => {
    const categories = new Set(books.value.map(book => book.category).filter(c => c))
    return Array.from(categories)
  }

  return {
    books,
    loading,
    error,
    fetchBooks,
    addBook,
    updateBook,
    deleteBook,
    searchByTitle,
    searchByCategory,
    getCategories
  }
})
