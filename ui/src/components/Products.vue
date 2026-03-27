<template>
    <div class="products-page">
        <h1>PRODUCTS</h1>

        <div v-if="isAdmin" class="admin-bar">
            <button class="upload-btn" @click="showUploadDialog = true">+ 上架商品</button>
        </div>

    <div class="filters-wrapper">
        <div class="filters">
        <input type="text" placeholder="搜索商品" v-model="searchParams.searchProductName" @keyup.enter="searchProduct">
        <select v-model="searchParams.searchProductPrice">
            <option value="">请选择价格范围</option>
            <option value="0-100">0-100元</option>
            <option value="100-500">100-500元</option>
            <option value="500-1000">500-1000元</option>  
        </select>
        <select v-model="searchParams.sortOrder">
            <option value="">默认排序</option>
            <option value="desc">价格由高到低</option>
            <option value="asc">价格由低到高</option>
        </select>
        <button @click="searchProduct">搜索</button>
    </div>
    </div>

     <div class="productslist">
        <div class="productcard" 
        v-for="item in pageProducts" :key="item.id">
            <img :src="item.image" alt="">
            <h3>{{ item.name }}</h3>
            <p>{{ item.description }}</p>
            <div>{{ item.price }}</div>
            <button @click="addProductToCart(item)">添加到购物车</button>
        </div>
     </div>

     <div class="page">
        <button class="page-btn arrow" :disabled="currentPage === 1" @click="currentPage--">&#8592;</button>
        <template v-for="page in totalPages" :key="page">
            <span v-if="page === 2 && currentPage > 4" class="ellipsis">···</span>
            <button
                v-if="page === 1 || page === totalPages || (page >= currentPage - 2 && page <= currentPage + 2)"
                :class="['page-btn', { active: currentPage === page }]"
                @click="currentPage = page"
            >
                {{ page }}
            </button>
            <span v-if="page === totalPages - 1 && currentPage < totalPages - 3" class="ellipsis">···</span>
        </template>
        <button class="page-btn arrow" :disabled="currentPage === totalPages" @click="currentPage++">&#8594;</button>
        <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
    </div>
    </div>

    <!-- 添加到购物车弹窗 -->
    <el-dialog title="添加商品" v-model="showAddDialog" width="30%">
        <el-form label-width="120px">
            <el-form-item label="商品名称">
                <el-input v-model="newProduct.productname" disabled />
            </el-form-item>
            <el-form-item label="单价">
                <el-input v-model="newProduct.price" disabled />
            </el-form-item>
            <el-form-item label="地址">
                <div style="display: flex; gap: 8px; width: 100%;">
                    <el-input 
                        v-model="newProduct.address" 
                        placeholder="请输入地址或点击定位"
                        style="flex: 1"
                    />
                    <el-button 
                        :loading="locating"
                        type="primary"
                        plain
                        @click="autoLocate"
                    >
                        {{ locating ? '定位中...' : '📍 定位' }}
                    </el-button>
                </div>
            </el-form-item>
            <el-form-item label="数量">
                <el-input-number v-model="newProduct.quantity" :min="1" :step="1" />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button type="primary" @click="confirmAdd">确认添加</el-button>
            <el-button @click="showAddDialog = false">取消</el-button>
        </template>
    </el-dialog>

    <!-- 上架商品弹窗 -->
    <el-dialog title="上架新商品" v-model="showUploadDialog" width="400px">
        <el-form label-width="90px">
            <el-form-item label="商品名称">
                <el-input v-model="uploadProduct.name" placeholder="请输入商品名称" />
            </el-form-item>
            <el-form-item label="单价">
                <el-input v-model="uploadProduct.price" placeholder="请输入单价" />
            </el-form-item>
            <el-form-item label="描述">
                <el-input v-model="uploadProduct.description" placeholder="请输入商品描述" />
            </el-form-item>
            <el-form-item label="商品图片">
                <div v-if="previewImage" class="preview-wrapper">
                    <img :src="previewImage" class="preview-img" />
                    <button class="remove-img" @click="removeImage">✕</button>
                </div>
                <div v-else class="upload-area" @click="triggerFileInput" @dragover.prevent @drop.prevent="handleDrop">
                    <input ref="fileInputRef" type="file" accept="image/*" style="display:none" @change="handleFileChange" />
                    <div class="upload-placeholder">
                        <span class="upload-icon">📁</span>
                        <p>点击或拖拽图片到此处</p>
                        <p class="upload-hint">支持 JPG、PNG、GIF</p>
                    </div>
                </div>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="showUploadDialog = false">取消</el-button>
            <el-button type="primary" @click="confirmUpload" :loading="uploading">确认上架</el-button>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const products = ref([])

const isAdmin = computed(() => {
    return localStorage.getItem('role') === 'ADMIN'
})

onMounted(async () => {
    try {
        const res = await axios.get('/api/goods')
        products.value = res.data.data || []
    } catch (error) {
        console.error('获取商品失败:', error)
    }
})

let currentPage = ref(1)
const pageSize = 8

const searchParams = ref({
    searchProductName: '',
    searchProductPrice: '',
    sortOrder: ''
})

const filteredProducts = computed(() => {
    let result = [...products.value]
    if (searchParams.value.searchProductName) {
        result = result.filter(item =>
            item.name.toLowerCase().includes(searchParams.value.searchProductName.toLowerCase())
        )
    }
    if (searchParams.value.searchProductPrice) {
        const [min, max] = searchParams.value.searchProductPrice.split('-').map(Number)
        result = result.filter(item => item.price >= min && item.price <= max)
    }
    if (searchParams.value.sortOrder === 'desc') {
        result.sort((a, b) => b.price - a.price)
    } else if (searchParams.value.sortOrder === 'asc') {
        result.sort((a, b) => a.price - b.price)
    }
    return result
})

const pageProducts = computed(() => {
    const start = (currentPage.value - 1) * pageSize
    return filteredProducts.value.slice(start, start + pageSize)
})

const totalPages = computed(() => Math.ceil(filteredProducts.value.length / pageSize))

const searchProduct = () => { currentPage.value = 1 }

watch(searchParams, () => {
    currentPage.value = 1
}, { deep: true })

// ========== 添加到购物车 ==========
let showAddDialog = ref(false)
let newProduct = ref({ productname: '', price: '', orderTime: '', address: '', quantity: 1 })

const addProductToCart = (product) => {
    newProduct.value = { productname: product.name, price: product.price, orderTime: '', address: '', quantity: 1 }
    showAddDialog.value = true
}

const confirmAdd = async () => {
    if (!newProduct.value.productname || !newProduct.value.price || !newProduct.value.address) {
        ElMessage.warning('请填写完整信息')
        return
    }
    const now = new Date()
    newProduct.value.orderTime = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
    try {
        const res = await axios.post('/api/add', newProduct.value)
        if (res.data.code === 200) {
            ElMessage.success('添加成功')
            showAddDialog.value = false
            flyToCart()
            newProduct.value = { productname: '', price: '', orderTime: '', address: '', quantity: 1 }
        } else {
            ElMessage.error('添加失败')
        }
    } catch (error) {
        console.error('添加失败:', error)
    }
}

// ========== 自动定位 ==========
const locating = ref(false)

const autoLocate = () => {
    if (!navigator.geolocation) {
        ElMessage.warning('当前浏览器不支持定位')
        return
    }
    locating.value = true
    navigator.geolocation.getCurrentPosition(
        async (pos) => {
            const { latitude, longitude } = pos.coords
            try {
                const res = await fetch(
                    `https://nominatim.openstreetmap.org/reverse?lat=${latitude}&lon=${longitude}&format=json&accept-language=zh`,
                    { headers: { 'User-Agent': 'ecommerce-app' } }
                )
                const data = await res.json()
                if (data && data.display_name) {
                const a = data.address
newProduct.value.address = [a.state, a.city, a.town, a.municipality, a.district, a.road, a.house_number]
    .filter(Boolean).join('')
                ElMessage.success('定位成功')
            } else {
                    newProduct.value.address = `${latitude.toFixed(6)}, ${longitude.toFixed(6)}`
                    ElMessage.warning('地址解析失败，已填入坐标')
                }
            } catch {
                newProduct.value.address = `${latitude.toFixed(6)}, ${longitude.toFixed(6)}`
                ElMessage.warning('网络异常，已填入坐标')
            } finally {
                locating.value = false
            }
        },
        (err) => {
            locating.value = false
            const msgs = {
                1: '已拒绝定位权限，请在浏览器设置中允许',
                2: '定位失败，请检查网络或设备',
                3: '定位超时，请重试'
            }
            ElMessage.error(msgs[err.code] || '定位失败')
        },
        { timeout: 10000, maximumAge: 60000 }
    )
}

// ========== 上架商品 ==========
const showUploadDialog = ref(false)
const uploading = ref(false)
const previewImage = ref('')
const fileInputRef = ref(null)
const selectedFile = ref(null)
const uploadProduct = ref({ name: '', price: '', description: '' })

const triggerFileInput = () => fileInputRef.value.click()

const handleFileChange = (e) => {
    const file = e.target.files[0]
    if (!file) return
    selectedFile.value = file
    previewImage.value = URL.createObjectURL(file)
}

const handleDrop = (e) => {
    const file = e.dataTransfer.files[0]
    if (!file) return
    selectedFile.value = file
    previewImage.value = URL.createObjectURL(file)
}

const removeImage = () => {
    previewImage.value = ''
    selectedFile.value = null
    if (fileInputRef.value) fileInputRef.value.value = ''
}

const confirmUpload = async () => {
    if (!uploadProduct.value.name || !uploadProduct.value.price || !uploadProduct.value.description) {
        ElMessage.warning('请填写完整信息')
        return
    }
    if (!selectedFile.value) {
        ElMessage.warning('请上传商品图片')
        return
    }
    uploading.value = true
    try {
        const formData = new FormData()
        formData.append('file', selectedFile.value)
        formData.append('name', uploadProduct.value.name)
        formData.append('price', uploadProduct.value.price)
        formData.append('description', uploadProduct.value.description)

        const res = await axios.post('/api/goods/add', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })
        if (res.data.code === 200) {
            ElMessage.success('上架成功')
            showUploadDialog.value = false
            const goodsRes = await axios.get('/api/goods')
            products.value = goodsRes.data.data || []
            uploadProduct.value = { name: '', price: '', description: '' }
            removeImage()
        } else {
            ElMessage.error('上架失败')
        }
    } catch (error) {
        console.error('上架失败:', error)
        ElMessage.error('上架失败')
    } finally {
        uploading.value = false
    }
}

// ========== 飞入购物车动画 ==========
const flyToCart = () => {
    const cartEl = document.querySelector('a[href="#/Cart"]')
    if (!cartEl) return
    const cartRect = cartEl.getBoundingClientRect()
    const dot = document.createElement('div')
    dot.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="#00AECB">
        <path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/>
        <line x1="3" y1="6" x2="21" y2="6" stroke="white" stroke-width="2"/>
        <path d="M16 10a4 4 0 01-8 0" stroke="white" stroke-width="2" fill="none"/>
    </svg>`
    dot.style.cssText = `position:fixed;z-index:9999;pointer-events:none;left:50%;top:50%;transform:translate(-50%,-50%);transition:all 1.5s cubic-bezier(0.25,0.46,0.45,0.94);`
    document.body.appendChild(dot)
    requestAnimationFrame(() => {
        requestAnimationFrame(() => {
            dot.style.left = cartRect.left + cartRect.width / 2 + 'px'
            dot.style.top = cartRect.top + cartRect.height / 2 + 'px'
            dot.style.transform = 'translate(-50%,-50%) scale(0.1)'
            dot.style.opacity = '0'
        })
    })
    setTimeout(() => dot.remove(), 1600)
}
</script>

<style>
* { margin: 0; padding: 0; }
h1 { font-weight: normal; font-size: 32px; }

.products-page {
    padding: 30px;
    background: var(--products-bg, #2c2f36);
    color: #fff;
    min-height: 100vh;
}

.admin-bar {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 16px;
}
.upload-btn {
    padding: 10px 20px;
    background: #00AECB;
    color: #fff;
    border: none;
    border-radius: 10px;
    font-size: 14px;
    cursor: pointer;
    transition: background 0.2s;
}
.upload-btn:hover { background: #0097b2; }

.filters-wrapper {
    background-color: var(--products-filter-bg, #25272E);
    padding: 20px;
    border-radius: 12px;
    margin-top: 10px;
    margin-bottom: 20px;
}
.filters { display: flex; gap: 15px; align-items: center; }
.filters input {
    border-radius: 8px;
    width: 700px;
    padding: 12px 16px;
    border: none;
    outline: none;
    background: var(--products-input-bg, #fff);
    color: var(--products-input-color, #111);
}
.filters select {
    border-radius: 8px;
    padding: 12px;
    width: 200px;
    border: none;
    outline: none;
    background: var(--products-input-bg, #fff);
    color: var(--products-input-color, #111);
}
.filters button { padding: 12px; background: aqua; border-radius: 8px; width: 100px; margin-left: 10px; }

.productslist {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
}
.productcard {
    background-color: var(--products-card-bg, #fff);
    color: var(--products-card-color, #333);
    padding: 16px;
    text-align: center;
    border-radius: 12px;
}
.productcard img { width: 100%; height: 180px; object-fit: cover; border-radius: 8px 8px 0 0; }
.productcard button { border-radius: 6px; padding: 8px; background-color: #00AECB; color: #fff; }
.productcard button:hover { background-color: #0097b2; }

.upload-area {
    width: 100%;
    height: 160px;
    border: 2px dashed #ddd;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: border-color 0.2s;
}
.upload-area:hover { border-color: #00AECB; }
.upload-placeholder { text-align: center; color: #aaa; }
.upload-icon { font-size: 32px; }
.upload-hint { font-size: 12px; margin-top: 4px; }
.preview-wrapper { position: relative; width: 100%; }
.preview-img { width: 100%; height: 160px; object-fit: cover; border-radius: 10px; }
.remove-img {
    position: absolute;
    top: 6px;
    right: 6px;
    background: rgba(0,0,0,0.5);
    color: #fff;
    border: none;
    border-radius: 50%;
    width: 24px;
    height: 24px;
    cursor: pointer;
    font-size: 12px;
}

.page { display: flex; gap: 6px; justify-content: center; align-items: center; margin-top: 30px; padding: 16px 0; }
.page-btn {
    min-width: 36px;
    height: 36px;
    padding: 0 10px;
    border: 1.5px solid var(--products-btn-border, #444);
    background-color: var(--products-btn-bg, #2c2f36);
    color: var(--products-btn-color, #ccc);
    border-radius: 8px;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.2s ease;
}
.page-btn:hover:not(:disabled) { background-color: #00AECB; border-color: #00AECB; color: #fff; }
.page-btn.active { background-color: #00AECB; border-color: #00AECB; color: #fff; font-weight: bold; box-shadow: 0 0 10px rgba(0,174,203,0.5); }
.page-btn.arrow { font-size: 16px; }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.ellipsis { color: #666; padding: 0 4px; line-height: 36px; }
.page-info { margin-left: 8px; color: #888; font-size: 13px; }
</style>