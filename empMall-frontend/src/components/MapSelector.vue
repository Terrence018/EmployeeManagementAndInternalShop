<script setup>
import { ref, watch } from 'vue'
import { GoogleMap, Marker } from 'vue3-google-map'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: Boolean, // 控制彈窗顯示
  apiKey: { type: String, required: true } //Google API Key
})

const emit = defineEmits(['update:visible', 'confirm-address'])

// 預設中心點
const center = ref({ lat: 25.033964, lng: 121.564468 })
const markerPosition = ref({ lat: 25.033964, lng: 121.564468 })
const selectedAddress = ref('')
const loading = ref(false)

// 監聽彈窗開啟，嘗試獲取使用者當前位置
watch(() => props.visible, (val) => {
  if (val) {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => {
        const userPos = {
          lat: position.coords.latitude,
          lng: position.coords.longitude
        }
        center.value = userPos
        markerPosition.value = userPos
        // 取得初始位置的地址
        geocodePosition(userPos)
      })
    }
  }
})

// 當點擊地圖時
const handleMapClick = (event) => {
  const newLat = event.latLng.lat()
  const newLng = event.latLng.lng()
  
  markerPosition.value = { lat: newLat, lng: newLng }
  geocodePosition(markerPosition.value)
}

// 呼叫 Google Geocoding API 將座標轉地址
const geocodePosition = (pos) => {
  loading.value = true
  const geocoder = new google.maps.Geocoder()
  
  geocoder.geocode({ location: pos }, (results, status) => {
    loading.value = false
    if (status === 'OK' && results[0]) {
      // 取第一個結果最準確
      selectedAddress.value = results[0].formatted_address
      // 移除前面的 "台灣" 或 "郵遞區號" 字樣讓地址短一點 (我先拿掉)
      // selectedAddress.value = selectedAddress.value.replace(/^台灣\d+\s*/, '')
    } else {
      selectedAddress.value = '無法解析此位置地址'
    }
  })
}

// 確認選擇
const handleConfirm = () => {
  if (!selectedAddress.value) {
    ElMessage.warning('請先選擇一個有效的位置')
    return
  }
  emit('confirm-address', selectedAddress.value)
  emit('update:visible', false) // 關閉彈窗
}

const handleClose = () => {
  emit('update:visible', false)
}
</script>

<template>
  <el-dialog
    v-model="props.visible"
    title="📍 請點擊地圖選擇收貨地點"
    width="600px"
    :before-close="handleClose"
    append-to-body
  >
    <div v-loading="loading" class="map-container">
      <GoogleMap
        :api-key="apiKey"
        style="width: 100%; height: 400px"
        :center="center"
        :zoom="15"
        @click="handleMapClick"
      >
        <Marker :options="{ position: markerPosition }" />
      </GoogleMap>
    </div>

    <div class="address-preview">
      <p style="margin: 0 0 5px 0; font-weight: bold; color: #606266;">目前選中地址：</p>
      <div class="address-text">{{ selectedAddress || '拖曳地圖點選位置...' }}</div>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
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
}
</style>