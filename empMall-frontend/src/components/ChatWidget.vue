<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { ElMessage, ElNotification, ElMessageBox } from 'element-plus'
import { ChatDotRound, Close, UserFilled, Promotion, Loading } from '@element-plus/icons-vue'

import SockJS from 'sockjs-client/dist/sockjs.min.js'
import Stomp from 'stompjs'
import request from '@/utils/request' 

const props = defineProps(['userRole', 'userName', 'uid']) 
const isVisible = ref(false) 
const chatState = ref('idle') 
let stompClient = null
let currentSubscription = null 

// 數據區 
const requestForm = reactive({ category: '', topic: '' })
const currentSessionId = ref(null) 
const requestList = ref([]) 
const messageList = ref([]) 
const inputMessage = ref('')
const msgBoxRef = ref(null)
const isSessionEnded = ref(false) 
const currentChatTargetName = ref('')
const selfEnded = ref(false) // 標記是否由「我自己」發起結束

// 動態主題 (保持不變)
const themeVars = computed(() => {
  if (props.userRole === 1) {
    return {
      '--theme-primary': '#409EFF',
      '--theme-gradient': 'linear-gradient(135deg, #409EFF 0%, #337ecc 100%)',
      '--theme-bg-light': '#f5f7fa',
      '--theme-border': '#d9ecff',
      '--theme-shadow': 'rgba(64, 158, 255, 0.4)',
      '--theme-text-guide': '#303133',
      '--theme-tag-bg': '#ecf5ff',
      '--theme-tag-text': '#409EFF',
      '--msg-me-bg': '#409EFF',
      '--msg-other-border': '#e4e7ed'
    }
  } else {
    return {
      '--theme-primary': '#FF8C00',
      '--theme-gradient': 'linear-gradient(135deg, #FF8C00 0%, #FF5E62 100%)',
      '--theme-bg-light': '#FFF7F0',
      '--theme-border': '#FFD8A8',
      '--theme-shadow': 'rgba(255, 140, 0, 0.4)',
      '--theme-text-guide': '#E65100',
      '--theme-tag-bg': '#FFF0E6',
      '--theme-tag-text': '#FF8C00',
      '--msg-me-bg': '#FF8C00',
      '--msg-other-border': '#FFD8A8'
    }
  }
})
const primaryColor = computed(() => props.userRole === 1 ? '#409EFF' : '#FF8C00')

// ----------------------------------------------------------------
// WebSocket 連線
const initWebSocket = () => {
  const socket = new SockJS('http://localhost:8080/ws-endpoint') 
  stompClient = Stomp.over(socket)
  stompClient.debug = null 

  stompClient.connect({}, (frame) => {
    console.log('✅ STOMP 連線成功')
    if (props.userRole === 1) {
      subscribeToAdminTopic()
      fetchWaitingList() 
    }
  }, (error) => {
    console.error('❌ STOMP 連線失敗:', error)
  })
}

const subscribeToAdminTopic = () => {
  stompClient.subscribe('/topic/admin/notifications', (msg) => {
    ElNotification.warning({ title: '新客服請求', message: msg.body })
    fetchWaitingList() 
  })
}

const subscribeToChatRoom = (sessionId) => {
  if (currentSubscription) currentSubscription.unsubscribe()
  isSessionEnded.value = false 
  selfEnded.value = false // 重置標記

  currentSubscription = stompClient.subscribe(`/topic/chat/${sessionId}`, (msg) => {
    const body = JSON.parse(msg.body)
    
    if (body.type === 'JOIN') {
      messageList.value.push({ sender: 'system', content: body.content })
      if (props.userRole === 2) {
        chatState.value = 'chatting' 
        ElMessage.success('客服人員已接通！')
      }
    } else if (body.type === 'LEAVE') {
      //判斷誰結束的
      isSessionEnded.value = true 
      
      let leaveMsg = ''
      
      // 邏輯判斷
      if (props.userRole === 1) {

        // 我是管理員，由我結束
        if (selfEnded.value) {
          leaveMsg = '管理員已結束對話'
        } else {
          // 否則是使用者結束
          leaveMsg = `使用者 ${currentChatTargetName.value || ''} 已結束對話`
        }
      } else {
        // 我是使用者，由我結束
        if (selfEnded.value) {
          leaveMsg = '使用者已結束對話'
        } else {
          // 否則由管理員結束
          leaveMsg = '管理員已結束對話'
        }
      }

      messageList.value.push({ sender: 'system', content: leaveMsg })
      
    } else {
      const isMe = body.sender === props.userName 
      messageList.value.push({
        sender: isMe ? 'me' : 'other',
        content: body.content,
        time: new Date().toLocaleTimeString() 
      })
    }
    scrollToBottom()
  })
}

// ----------------------------------------------------------------
// 操作流程
const sendRequest = async () => {
  if (!requestForm.category || !requestForm.topic) return ElMessage.warning('請選擇問題類型並輸入描述')
  try {
    const res = await request.post('/chat/start', {
      userId: props.uid, userName: props.userName, category: requestForm.category, topic: requestForm.topic
    })
    if (res) {
      currentSessionId.value = res
      chatState.value = 'waiting' 
      subscribeToChatRoom(res)
    }
  } catch (err) { ElMessage.error('發送請求失敗') }
}

const fetchWaitingList = async () => {
  try {
    const res = await request.get('/chat/waiting')
    requestList.value = res
  } catch (err) {}
}

const acceptRequest = async (session) => {
  try {
    await request.put(`/chat/accept/${session.id}?adminId=${props.uid}`)
    currentSessionId.value = session.id
    currentChatTargetName.value = session.userName 
    chatState.value = 'chatting'
    subscribeToChatRoom(session.id)
    loadHistory(session.id)
  } catch (err) { ElMessage.error('接單失敗') }
}

const confirmEndChat = () => {
  if (isSessionEnded.value) {
    handleChatEnd()
    return
  }
  ElMessageBox.confirm('確定要結束這次的對話嗎？', '結束確認', {
    confirmButtonText: '確定結束', cancelButtonText: '取消', type: 'warning'
  }).then(() => { endChat() }).catch(() => {})
}

const endChat = async () => {
  if(!currentSessionId.value) return
  
  // 標記：這是我主動結束的
  selfEnded.value = true 
  
  try { await request.put(`/chat/close/${currentSessionId.value}`) } catch (err) {}
}

const sendMessage = () => {
  if (isSessionEnded.value || !inputMessage.value.trim() || !stompClient || !currentSessionId.value) return
  const chatMessage = { sessionId: currentSessionId.value, sender: props.userName, content: inputMessage.value, type: 'CHAT' }
  stompClient.send("/app/sendMessage", {}, JSON.stringify(chatMessage))
  inputMessage.value = ''
}

const handleCloseWindow = () => {
  if (props.userRole === 1) {
    if (isSessionEnded.value) handleChatEnd() 
    isVisible.value = false
    return
  }

  if (chatState.value === 'idle' && !isSessionEnded.value) {
    isVisible.value = false
    return
  }

  ElMessageBox.confirm(
    '確定要關閉對話視窗嗎？\n關閉後對話內容將不可恢復。', 
    '關閉確認', 
    { confirmButtonText: '確定關閉', cancelButtonText: '取消', type: 'warning', center: true }
  ).then(() => {
    if (!isSessionEnded.value && currentSessionId.value) {
      endChat() 
    }
    handleChatEnd() 
    isVisible.value = false 
  }).catch(() => {})
}

const handleChatEnd = () => {
  chatState.value = 'idle'
  messageList.value = []
  currentSessionId.value = null
  requestForm.topic = '' 
  isSessionEnded.value = false
  selfEnded.value = false
  currentChatTargetName.value = ''
  if(props.userRole === 1) fetchWaitingList()
}

const loadHistory = async (sid) => {
  try {
    const res = await request.get(`/chat/history/${sid}`)
    messageList.value = res.map(log => ({
      sender: log.senderName === props.userName ? 'me' : 'other',
      content: log.content,
      time: log.createTime 
    }))
    scrollToBottom()
  } catch(err) {}
}

const scrollToBottom = () => {
  nextTick(() => { if (msgBoxRef.value) msgBoxRef.value.scrollTop = msgBoxRef.value.scrollHeight })
}

onMounted(() => { initWebSocket() })
onUnmounted(() => { if (stompClient) stompClient.disconnect() })
</script>

<template>
  <div class="chat-widget" :style="themeVars">
    
    <div class="chat-fab" @click="isVisible = !isVisible">
      <el-badge :value="userRole === 1 ? requestList.length : 0" :hidden="requestList.length === 0" class="item">
        <div class="fab-circle">
          <el-icon :size="32" color="white"><ChatDotRound /></el-icon>
        </div>
      </el-badge>
    </div>

    <transition name="el-zoom-in-bottom">
      <div v-if="isVisible" class="chat-window">
        
        <div class="chat-header">
          <span>
            <el-icon style="vertical-align: middle; margin-right: 5px;"><ChatDotRound /></el-icon> 
            {{ userRole === 1 ? '客服中心 (管理員)' : '線上客服' }}
          </span>
          <el-icon class="close-btn" @click="handleCloseWindow"><Close /></el-icon>
        </div>

        <div class="chat-body">
          
          <div v-if="userRole === 2 && chatState === 'idle'" class="form-view">
            <p class="guide-text">👋 您好，{{ userName }}<br>很高興為您服務！</p>
            <el-select v-model="requestForm.category" placeholder="請選擇問題類型" style="width: 100%; margin-bottom: 15px;" size="large">
              <el-option label="修改個人資料" value="個人資料" />
              <el-option label="訂單相關問題" value="訂單" />
              <el-option label="積分/兌換問題" value="積分" />
              <el-option label="其他問題" value="其他" />
            </el-select>
            <el-input v-model="requestForm.topic" type="textarea" :rows="5" placeholder="請詳細描述您的問題..." resize="none"/>
            <el-button :color="primaryColor" style="color: white" class="start-btn" size="large" round @click="sendRequest">
              開始諮詢 <el-icon class="el-icon--right"><Promotion /></el-icon>
            </el-button>
          </div>

          <div v-else-if="userRole === 2 && chatState === 'waiting'" class="waiting-view">
            <el-icon class="is-loading" :size="50" :color="primaryColor"><Loading /></el-icon>
            <p style="margin-top: 20px; font-weight: bold; color: var(--theme-text-guide)">正在呼叫專員...</p>
            <p class="sub-text">請稍候，我們將盡快回應</p>
          </div>

          <div v-else-if="userRole === 1 && chatState === 'idle'" class="admin-list-view">
            <div v-if="requestList.length === 0" class="empty-tip">
              <el-empty description="目前沒有待處理的請求" :image-size="100"></el-empty>
            </div>
            <div v-for="req in requestList" :key="req.id" class="req-card">
              <div class="req-header">
                <span class="name"><el-icon><UserFilled /></el-icon> {{ req.userName }}</span>
                <span class="tag">{{ req.category }}</span>
              </div>
              <p class="req-q">{{ req.topic }}</p>
              <div style="text-align: right;">
                <el-button :color="primaryColor" style="color: white" size="small" round @click="acceptRequest(req)">立即回覆</el-button>
              </div>
            </div>
          </div>

          <div v-else-if="chatState === 'chatting'" class="chat-view">
            <div class="msg-container" ref="msgBoxRef">
              <div 
                v-for="(msg, idx) in messageList" 
                :key="idx" 
                :class="['msg-row', msg.sender === 'me' ? 'msg-me' : (msg.sender === 'system' ? 'msg-sys' : 'msg-other')]"
              >
                <div class="bubble">
                  {{ msg.content }}
                </div>
              </div>
            </div>
            
            <div class="input-area">
              <el-input 
                v-model="inputMessage" 
                :placeholder="isSessionEnded ? '對話已結束' : '輸入訊息...'" 
                :disabled="isSessionEnded"
                @keyup.enter="sendMessage" 
                size="large"
              >
                <template #suffix>
                  <el-icon 
                    class="send-icon" 
                    :style="{ cursor: isSessionEnded ? 'not-allowed' : 'pointer', opacity: isSessionEnded ? 0.5 : 1 }"
                    @click="sendMessage"
                  ><Promotion /></el-icon>
                </template>
              </el-input>
              <el-button v-if="userRole === 1" type="danger" link size="small" @click="confirmEndChat" style="margin-top:5px; width: 100%;">
                {{ isSessionEnded ? '關閉視窗' : '結束對話' }}
              </el-button>
            </div>
          </div>

        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
.chat-widget { position: fixed; bottom: 30px; right: 30px; z-index: 1500; }
.fab-circle { width: 60px; height: 60px; background: var(--theme-gradient); border-radius: 50%; display: flex; justify-content: center; align-items: center; box-shadow: 0 4px 15px var(--theme-shadow); cursor: pointer; transition: transform 0.3s; animation: pulse 2s infinite; }
.fab-circle:hover { transform: scale(1.1); }
@keyframes pulse { 0% { box-shadow: 0 0 0 0 var(--theme-shadow); } 70% { box-shadow: 0 0 0 10px rgba(0,0,0,0); } 100% { box-shadow: 0 0 0 0 rgba(0,0,0,0); } }
.chat-window { position: absolute; bottom: 80px; right: 0; width: 360px; height: 520px; background: white; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,0.15); display: flex; flex-direction: column; overflow: hidden; border: 1px solid var(--theme-border); }
.chat-header { background: var(--theme-gradient); color: white; padding: 15px 20px; font-size: 16px; font-weight: bold; display: flex; justify-content: space-between; align-items: center; }
.close-btn { cursor: pointer; transition: transform 0.2s; }
.close-btn:hover { transform: rotate(90deg); }
.chat-body { flex: 1; padding: 20px; display: flex; flex-direction: column; background: var(--theme-bg-light); overflow: hidden; }
.guide-text { font-size: 18px; color: var(--theme-text-guide); font-weight: bold; margin-bottom: 20px; line-height: 1.5; }
.start-btn { width: 100%; margin-top: auto; font-weight: bold; box-shadow: 0 4px 10px var(--theme-shadow); }
.req-card { background: white; padding: 15px; margin-bottom: 12px; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); transition: transform 0.2s; border-left: 4px solid var(--theme-primary); }
.req-card:hover { transform: translateY(-2px); }
.req-header { display: flex; justify-content: space-between; margin-bottom: 8px; font-weight: bold; color: #303133; }
.tag { font-size: 12px; color: var(--theme-tag-text); background: var(--theme-tag-bg); padding: 2px 8px; border-radius: 10px; }
.req-q { font-size: 14px; color: #606266; margin-bottom: 15px; line-height: 1.4; }
.msg-me .bubble { background: var(--msg-me-bg); color: white; border-top-right-radius: 2px; }
.msg-other .bubble { background: white; color: #333; border-top-left-radius: 2px; border: 1px solid var(--msg-other-border); }
.msg-sys { font-size: 12px; color: #909399; background: rgba(0,0,0,0.05); padding: 2px 10px; border-radius: 10px; display: inline-block; }
.bubble { padding: 10px 14px; border-radius: 12px; max-width: 75%; font-size: 14px; line-height: 1.5; word-wrap: break-word; box-shadow: 0 1px 2px rgba(0,0,0,0.05); }
.input-area { border-top: 1px solid var(--theme-border); padding-top: 15px; }
.send-icon { cursor: pointer; color: var(--theme-primary); font-size: 18px; transition: color 0.2s; }
.send-icon:hover { opacity: 0.8; }
.form-view { height: 100%; display: flex; flex-direction: column; }
.waiting-view { display: flex; flex-direction: column; justify-content: center; align-items: center; height: 100%; text-align: center; color: #606266; }
.sub-text { font-size: 13px; color: #909399; margin-top: 5px; }
.admin-list-view { overflow-y: auto; height: 100%; padding-right: 5px; }
.chat-view { display: flex; flex-direction: column; height: 100%; }
.msg-container { flex: 1; overflow-y: auto; margin-bottom: 15px; padding-right: 5px; }
.msg-row { display: flex; margin-bottom: 15px; }
.msg-me { justify-content: flex-end; }
.msg-other { justify-content: flex-start; }
.msg-sys { justify-content: center; margin: 10px 0; }
</style>