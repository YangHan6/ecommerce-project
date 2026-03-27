<template>
  <div class="products-wrapper">
    <!-- 搜索栏 -->
    <div class="filters">
      <input
        type="text"
        placeholder="搜索商品名称..."
        v-model="searchParams.searchProductName"
        @keyup.enter="searchProduct"
      />
      <select v-model="searchParams.searchProductPrice">
        <option value="">全部价格</option>
        <option value="0-100">0 - 100 元</option>
        <option value="100-500">100 - 500 元</option>
        <option value="500-1000">500 - 1000 元</option>
      </select>
      <select v-model="searchParams.sortOrder">
        <option value="">默认排序</option>
        <option value="asc">价格 ↑ 低到高</option>
        <option value="desc">价格 ↓ 高到低</option>
      </select>
      <button class="search-btn" @click="searchProduct">搜索</button>
    </div>

    <!-- 表格 -->
    <div class="table-container">
      <table class="product-table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>商品名称</th>
            <th>单价</th>
            <th>下单时间</th>
            <th>地址</th>
            <th>数量</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in Products" :key="item.id">
            <td class="id-cell">{{ item.id }}</td>
            <td class="name-cell">{{ item.productname }}</td>
            <td class="price-cell">¥ {{ item.price }}</td>
            <td class="muted">{{ item.orderTime }}</td>
            <td class="muted address-cell">{{ item.address }}</td>
            <td>{{ item.quantity }}</td>
            <td class="action-cell">
              <button class="btn-edit" @click="updateProduct(item)">编辑</button>
              <button class="btn-delete" @click="removeProduct(item)">删除</button>
            </td>
          </tr>
          <tr v-if="Products.length === 0">
            <td colspan="7" class="empty">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 更新弹窗 -->
    <el-dialog title="更新商品" v-model="showUpdateDialog" width="400px">
      <el-form label-width="90px" v-model="updateProductToDelete">
        <el-form-item label="商品名称">
          <el-input v-model="updateProductToDelete.productname" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input v-model="updateProductToDelete.price" placeholder="请输入单价" />
        </el-form-item>
        <el-form-item label="下单时间">
          <el-date-picker
            v-model="updateProductToDelete.orderTime"
            type="date"
            placeholder="请选择下单时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="updateProductToDelete.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="updateProductToDelete.quantity" min="0" step="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUpdateDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmUpdate">确认更新</el-button>
      </template>
    </el-dialog>

    <!-- 删除确认弹窗 -->
    <el-dialog title="确认删除" v-model="showRemoveDialog" width="360px">
      <p style="color: #666; font-size: 14px;">此操作不可逆，确定要删除该商品吗？</p>
      <template #footer>
        <el-button @click="showRemoveDialog = false">取消</el-button>
        <el-button type="danger" @click="confirmRemove">确认删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const Products = ref([])
const searchParams = ref({
  searchProductName: '',
  searchProductPrice: '',
  sortOrder: ''
})

const searchProduct = async () => {
  try {
    const res = await axios.post('/api/search', searchParams.value)
    Products.value = res.data.data
  } catch (error) {
    console.error('搜索失败:', error)
  }
}

const getProducts = async () => {
  try {
    const res = await axios.get('/api/products')
    Products.value = res.data.data
  } catch (error) {
    console.error('获取商品失败:', error)
  }
}

onMounted(() => { getProducts() })

const showUpdateDialog = ref(false)
const updateProductToDelete = ref({})
const updateProduct = (product) => {
  updateProductToDelete.value = { ...product }
  showUpdateDialog.value = true
}
const confirmUpdate = async () => {
  try {
    const res = await axios.post('/api/update', updateProductToDelete.value)
    if (res.data.code == 200) {
      ElMessage.success('更新成功')
    } else {
      ElMessage.error('更新失败')
    }
    getProducts()
    showUpdateDialog.value = false
  } catch (error) {
    console.error('更新失败:', error)
  }
}

const showRemoveDialog = ref(false)
const removeProductToDelete = ref({})
const removeProduct = (product) => {
  removeProductToDelete.value = product
  showRemoveDialog.value = true
}
const confirmRemove = async () => {
  try {
    const res = await axios.post(`/api/remove/${removeProductToDelete.value.id}`)
    if (res.data.code == 200) {
      ElMessage.success('删除成功')
    } else {
      ElMessage.error('删除失败')
    }
    getProducts()
    showRemoveDialog.value = false
  } catch (error) {
    console.error('删除失败:', error)
  }
}
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.products-wrapper {
  padding: 32px 40px;
  font-family: -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background: var(--bg-color);
  min-height: 100vh;
}

/* 搜索栏 */
.filters {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
}

.filters input,
.filters select {
  padding: 9px 14px;
  border: 1px solid var(--border-color);
  border-radius: 10px;
  font-size: 13px;
  font-family: inherit;
  color: var(--text-color);
  background: var(--card-bg);
  outline: none;
  transition: border-color 0.2s;
}

.filters input {
  flex: 1;
}

.filters input:focus,
.filters select:focus {
  border-color: var(--text-color);
}

.search-btn {
  padding: 9px 20px;
  background: var(--text-color);
  color: var(--card-bg);
  border: none;
  border-radius: 10px;
  font-size: 13px;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.search-btn:hover {
  opacity: 0.8;
  transform: translateY(-1px);
}

/* 表格容器 */
.table-container {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  overflow: hidden;
}

.product-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

thead tr {
  background: var(--table-header-bg);
  border-bottom: 1px solid var(--border-color);
}

thead th {
  padding: 14px 16px;
  text-align: left;
  font-size: 11px;
  font-weight: 600;
  color: var(--muted-color);
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

tbody tr {
  border-bottom: 1px solid var(--border-color);
  transition: background 0.15s;
}

tbody tr:last-child {
  border-bottom: none;
}

tbody tr:hover {
  background: var(--row-hover-bg);
}

td {
  padding: 14px 16px;
  color: var(--text-color);
  vertical-align: middle;
}

.id-cell {
  color: var(--muted-color);
  font-size: 12px;
}

.name-cell {
  font-weight: 500;
  color: var(--text-color);
}

.price-cell {
  color: #e74c3c;
  font-weight: 600;
}

.muted {
  color: var(--muted-color);
}

.address-cell {
  max-width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.empty {
  text-align: center;
  color: var(--muted-color);
  padding: 40px;
}

/* 操作按钮 */
.action-cell {
  display: flex;
  gap: 8px;
}

.btn-edit,
.btn-delete {
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 12px;
  font-family: inherit;
  cursor: pointer;
  border: 1px solid;
  transition: all 0.15s;
}

.btn-edit {
  background: var(--card-bg);
  color: var(--text-color);
  border-color: var(--border-color);
}

.btn-edit:hover {
  background: var(--text-color);
  color: var(--card-bg);
  border-color: var(--text-color);
}

.btn-delete {
  background: var(--card-bg);
  color: #e74c3c;
  border-color: #fde8e8;
}

.btn-delete:hover {
  background: #e74c3c;
  color: #fff;
  border-color: #e74c3c;
}
</style>