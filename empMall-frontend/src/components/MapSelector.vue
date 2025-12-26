<script setup>
import { ref, watch, computed } from 'vue'
import { GoogleMap, Marker } from 'vue3-google-map'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: Boolean,
  apiKey: { type: String, required: true }
})

const emit = defineEmits(['update:visible', 'confirm-address'])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const center = ref({ lat: 25.033964, lng: 121.564468 })
const markerPosition = ref({ lat: 25.033964, lng: 121.564468 })
const selectedAddress = ref('')
const loading = ref(false)
const mapReady = ref(false) // 新增：標記地圖是否載入完成

// --- 修正 1: 當地圖元件載入完成後觸發 ---
const onMapReady = () => {
  mapReady.value = true
  // 地圖準備好後，才開始執行定位邏輯
  initUserLocation()
}

// 初始化使用者位置
const initUserLocation = () => {
  // 如果地圖還沒好，就不執行 (保險起見)
  if (!mapReady.value) return

  loading.value = true
  selectedAddress.value = ''

  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
        (position) => {
          const userPos = {
            lat: position.coords.latitude,
            lng: position.coords.longitude
          }
          center.value = userPos
          markerPosition.value = userPos
          geocodePosition(userPos)
        },
        (error) => {
          console.warn('無法獲取定位:', error)
          geocodePosition(markerPosition.value)
        }
    )
  } else {
    geocodePosition(markerPosition.value)
  }
}

// 監聽彈窗開啟
// --- 修正 2: 這裡只負責重置狀態，不負責執行定位 ---
// 如果地圖已經是 Ready 狀態 (第二次打開彈窗)，才手動觸發定位
watch(() => props.visible, (val) => {
  if (val && mapReady.value) {
    initUserLocation()
  }
})

const handleMapClick = (event) => {
  if (!event.latLng) return
  const newLat = event.latLng.lat()
  const newLng = event.latLng.lng()
  markerPosition.value = { lat: newLat, lng: newLng }
  geocodePosition(markerPosition.value)
}

const geocodePosition = (pos) => {
  loading.value = true

  // --- 修正 3: 安全檢查，確保 Geocoder 真的存在 ---
  if (!window.google || !window.google.maps || !window.google.maps.Geocoder) {
    console.warn('Google Maps Geocoder 尚未準備好')
    // 如果這裡發生了，通常是因為 mapReady 判斷失誤，稍微延遲重試
    setTimeout(() => geocodePosition(pos), 500)
    return
  }

  try {
    const geocoder = new google.maps.Geocoder()
    geocoder.geocode({ location: pos }, (results, status) => {
      loading.value = false // 確保這裡一定會執行關閉 loading
      if (status === 'OK' && results[0]) {
        selectedAddress.value = results[0].formatted_address
      } else {
        selectedAddress.value = '無法解析此位置地址'
        console.error('Geocoder failed: ' + status)
      }
    })
  } catch (e) {
    // --- 修正 4: 捕捉同步錯誤，防止轉圈圈卡死 ---
    loading.value = false
    console.error('Geocode 執行錯誤:', e)
    ElMessage.error('地圖元件發生錯誤，請重新整理試試')
  }
}

const handleConfirm = () => {
  if (!selectedAddress.value) {
    ElMessage.warning('請先選擇一個有效的位置')
    return
  }
  emit('confirm-address', selectedAddress.value)
  dialogVisible.value = false
}
</script>

<template>
  <el-dialog
      v-model="dialogVisible"
      title="📍 請點擊地圖選擇收貨地點"
      width="600px"
      append-to-body
  >
    <div v-loading="loading" class="map-container">
      <GoogleMap
          :api-key="apiKey"
          style="width: 100%; height: 400px"
          :center="center"
          :zoom="15"
          map-id="DEMO_MAP_ID"
          @click="handleMapClick"
          @ready="onMapReady"
      >
        <Marker :options="{ position: markerPosition }" />
      </GoogleMap>
    </div>

    <div class="address-preview">
      <p style="margin: 0 0 5px 0; font-weight: bold; color: #606266;">目前選中地址：</p>
      <div class="address-text">{{ selectedAddress || '地圖載入中...' }}</div>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :disabled="!selectedAddress">
          確認此地點
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped>
.map-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}
.address-preview {
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}
.address-text {
  font-size: 16px;
  color: #409EFF;
  font-weight: bold;
  min-height: 24px;
}
</style>