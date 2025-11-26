<template>
  <div class="app-container">
    <header class="app-header">
      <h1>敏感数据识别系统</h1>
    </header>
    
    <main class="app-main">
      <div class="input-section">
        <h2>文本输入</h2>
        <el-input
          v-model="inputText"
          type="textarea"
          :rows="10"
          placeholder="请输入需要检测的文本内容..."
          @input="handleInput"
        ></el-input>
        <div class="input-actions">
          <el-button type="primary" @click="detectSensitiveData">开始检测</el-button>
          <el-button @click="clearInput">清空</el-button>
          <el-button @click="copyResult" :disabled="!hasResults">复制结果</el-button>
        </div>
      </div>
      
      <div class="result-section">
        <h2>检测结果
          <span v-if="hasResults" class="result-count">
            (共检测到 {{ detectionResult.totalDetected }} 项敏感数据)
          </span>
        </h2>
        
        <div class="result-filters" v-if="hasResults">
          <el-tag
            v-for="type in sensitiveDataTypes"
            :key="type.code"
            :color="getTagColor(type.riskLevel)"
            :effect="isTypeSelected(type.code) ? 'dark' : 'plain'"
            @click="toggleTypeFilter(type.code)"
            class="filter-tag"
          >
            {{ type.name }}
            <el-badge :value="getTypeCount(type.code)" class="type-badge" />
          </el-tag>
        </div>
        
        <div class="result-content" v-if="hasResults">
          <div class="highlighted-text">
            <span
              v-for="(segment, index) in highlightedSegments"
              :key="index"
              :class="getSegmentClass(segment)"
              :style="getSegmentStyle(segment)"
            >
              {{ segment.text }}
            </span>
          </div>
          
          <el-divider>敏感数据详情</el-divider>
          
          <el-table :data="filteredResults" style="width: 100%">
            <el-table-column prop="content" label="内容" min-width="200">
              <template #default="scope">
                <el-popover
                  placement="top"
                  title="原始内容"
                  :width="300"
                  trigger="hover"
                >
                  <template #reference>
                    <span class="sensitive-content">{{ scope.row.content }}</span>
                  </template>
                  {{ scope.row.content }}
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column prop="type.name" label="类型" width="120">
              <template #default="scope">
                <el-tag
                  :type="getTagType(scope.row.type.riskLevel.name)"
                  size="small"
                >
                  {{ scope.row.type.name }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="type.riskLevel.name" label="风险等级" width="100">
              <template #default="scope">
                <el-tag
                  :type="getTagType(scope.row.type.riskLevel.name)"
                  size="small"
                >
                  {{ scope.row.type.riskLevel.name }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="suggestion" label="处理建议" min-width="250">
              <template #default="scope">
                <el-tooltip
                  placement="top"
                  :content="scope.row.suggestion"
                  :show-after="500"
                  :hide-after="0"
                >
                  <span class="suggestion-text">{{ truncateText(scope.row.suggestion, 50) }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div class="empty-result" v-else-if="detectionAttempted">
          <el-empty description="未检测到敏感数据"></el-empty>
        </div>
        
        <div class="loading-result" v-if="isLoading">
          <el-skeleton :rows="10" animated />
        </div>
      </div>
    </main>
    
    <footer class="app-footer">
      <p>© 2023 敏感数据识别系统 - 保护您的数据安全</p>
    </footer>
  </div>
</template>

<script>
import axios from 'axios'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

export default {
  name: 'App',
  data: function() {
    return {
      inputText: '',
      detectionResult: {
        originalText: '',
        detectedItems: [],
        totalDetected: 0,
        processingTimeMs: 0
      },
      sensitiveDataTypes: [],
      selectedTypes: [],
      isLoading: false,
      detectionAttempted: false,
      stompClient: null,
      realtimeDetectionEnabled: true,
      debounceTimer: null
    }
  },
  computed: {
    hasResults: function() {
      return this.detectionResult.detectedItems && this.detectionResult.detectedItems.length > 0
    },
    filteredResults: function() {
      if (!this.hasResults || this.selectedTypes.length === 0) {
        return this.detectionResult.detectedItems
      }
      return this.detectionResult.detectedItems.filter(function(item) {
        return this.selectedTypes.includes(item.type.code)
      }, this)
    },
    highlightedSegments: function() {
      if (!this.hasResults) return [{ text: this.inputText, isSensitive: false }]
      
      var segments = []
      var lastIndex = 0
      
      // 按位置排序敏感数据项
      var sortedItems = this.detectionResult.detectedItems.slice().sort(function(a, b) {
        return a.startPosition - b.startPosition
      })
      
      for (var i = 0; i < sortedItems.length; i++) {
        var item = sortedItems[i]
        // 添加敏感数据前的普通文本
        if (item.startPosition > lastIndex) {
          segments.push({
            text: this.inputText.substring(lastIndex, item.startPosition),
            isSensitive: false
          })
        }
        
        // 添加敏感数据
        segments.push({
          text: item.content,
          isSensitive: true,
          type: item.type,
          item: item
        })
        
        lastIndex = item.endPosition
      }
      
      // 添加最后一段普通文本
      if (lastIndex < this.inputText.length) {
        segments.push({
          text: this.inputText.substring(lastIndex),
          isSensitive: false
        })
      }
      
      return segments
    }
  },
  mounted: function() {
    this.fetchSensitiveDataTypes()
    this.initWebSocket()
  },
  beforeUnmount: function() {
    this.disconnectWebSocket()
  },
  methods: {
    // 检测敏感数据
    detectSensitiveData: function() {
      var self = this
      if (!this.inputText.trim()) {
        this.$message.warning('请输入需要检测的文本内容')
        return
      }
      
      this.isLoading = true
      this.detectionAttempted = true
      
      axios.post('/api/detect/text', this.inputText, {
        headers: {
          'Content-Type': 'text/plain'
        }
      }).then(function(response) {
        // 处理后端返回的数据，确保数据结构与前端期望一致
        var rawResult = response.data
        var processedItems = []
        
        if (rawResult.detectedItems && rawResult.detectedItems.length > 0) {
          for (var i = 0; i < rawResult.detectedItems.length; i++) {
            var item = rawResult.detectedItems[i]
            var processedItem = {}
            processedItem.content = item.content || ''
            processedItem.startPosition = item.startPosition || 0
            processedItem.endPosition = item.endPosition || 0
            
            // 标准化type对象 - 处理后端返回字符串类型的情况
            processedItem.type = {}
            
            if (typeof item.type === 'string') {
              // 当type是字符串时，根据类型代码映射为对应的名称
              var typeCode = item.type
              processedItem.type.code = typeCode
              
              // 映射类型代码到中文名称
              var typeNameMap = {
                'BANK_CARD': '银行卡号',
                'BANK_ACCOUNT': '银行账号',
                'ID_CARD': '身份证号',
                'PHONE_NUMBER': '手机号',
                'EMAIL': '邮箱',
                'PASSPORT': '护照'
              }
              processedItem.type.name = typeNameMap[typeCode] || typeCode
              processedItem.type.riskLevel = {
                name: '高风险' // 默认风险等级
              }
            } else {
              // 原来的对象处理逻辑
              processedItem.type.name = item.type && item.type.name ? item.type.name : ''
              processedItem.type.code = item.type && item.type.code ? item.type.code : ''
              processedItem.type.riskLevel = {}
              processedItem.type.riskLevel.name = item.type && item.type.riskLevel && item.type.riskLevel.name ? item.type.riskLevel.name : '未知风险'
            }
            
            // 确保suggestion存在
            processedItem.suggestion = item.suggestion ? item.suggestion : self.getDefaultSuggestion(processedItem.type.name)
            
            processedItems.push(processedItem)
          }
        }
        
        self.detectionResult = {
          originalText: rawResult.originalText || '',
          detectedItems: processedItems,
          totalDetected: processedItems.length,
          processingTimeMs: rawResult.processingTimeMs || 0
        }
        
        self.$message.success('检测完成，耗时 ' + self.detectionResult.processingTimeMs + 'ms')
      }).catch(function(error) {
        console.error('检测敏感数据失败:', error)
        self.$message.error('检测失败，请稍后重试')
      }).finally(function() {
        self.isLoading = false
      })
    },
    
    // 初始化WebSocket连接
    initWebSocket: function() {
      if (!this.realtimeDetectionEnabled) return
      
      try {
        var socket = new SockJS('/ws')
        this.stompClient = Stomp.over(socket)
        var self = this
        
        this.stompClient.connect({}, function() {
          console.log('WebSocket连接已建立')
          
          self.stompClient.subscribe('/topic/detectionResults', function(message) {
            try {
              var rawResult = JSON.parse(message.body)
              var processedItems = []
              
              if (rawResult.detectedItems && rawResult.detectedItems.length > 0) {
                for (var i = 0; i < rawResult.detectedItems.length; i++) {
                  var item = rawResult.detectedItems[i]
                  var processedItem = {}
                  processedItem.content = item.content || ''
                  processedItem.startPosition = item.startPosition || 0
                  processedItem.endPosition = item.endPosition || 0
                  
                  // 标准化type对象 - 处理后端返回字符串类型的情况
                  processedItem.type = {}
                  
                  if (typeof item.type === 'string') {
                    // 当type是字符串时，根据类型代码映射为对应的名称
                    var typeCode = item.type
                    processedItem.type.code = typeCode
                    
                    // 映射类型代码到中文名称
                    var typeNameMap = {
                      'BANK_CARD': '银行卡号',
                      'BANK_ACCOUNT': '银行账号',
                      'ID_CARD': '身份证号',
                      'PHONE_NUMBER': '手机号',
                      'EMAIL': '邮箱',
                      'PASSPORT': '护照'
                    }
                    processedItem.type.name = typeNameMap[typeCode] || typeCode
                    processedItem.type.riskLevel = {
                      name: '高风险' // 默认风险等级
                    }
                  } else {
                    // 原来的对象处理逻辑
                    processedItem.type.name = item.type && item.type.name ? item.type.name : ''
                    processedItem.type.code = item.type && item.type.code ? item.type.code : ''
                    processedItem.type.riskLevel = {}
                    processedItem.type.riskLevel.name = item.type && item.type.riskLevel && item.type.riskLevel.name ? item.type.riskLevel.name : '未知风险'
                  }
                  
                  // 确保suggestion存在
                  processedItem.suggestion = item.suggestion ? item.suggestion : self.getDefaultSuggestion(processedItem.type.name)
                  
                  processedItems.push(processedItem)
                }
              }
              
              var processedResult = {
                originalText: rawResult.originalText || '',
                detectedItems: processedItems,
                totalDetected: processedItems.length,
                processingTimeMs: rawResult.processingTimeMs || 0
              }
              
              // 只在没有手动检测结果时使用实时检测结果
              if (!self.detectionAttempted) {
                self.detectionResult = processedResult
              }
            } catch (e) {
              console.error('解析WebSocket消息失败:', e)
            }
          })
        }, function(error) {
          console.error('WebSocket连接失败:', error)
        })
      } catch (error) {
        console.error('初始化WebSocket失败:', error)
      }
    },
    
    // 获取敏感数据类型列表
    fetchSensitiveDataTypes: function() {
      var self = this
      axios.get('/api/detect/types').then(function(response) {
        self.sensitiveDataTypes = response.data
      }).catch(function(error) {
        console.error('获取敏感数据类型失败:', error)
        self.$message.error('获取敏感数据类型失败')
      })
    },
    
    // 断开WebSocket连接
    disconnectWebSocket: function() {
      if (this.stompClient) {
        this.stompClient.disconnect()
        console.log('WebSocket连接已断开')
      }
    },
    
    // 处理输入事件（用于实时检测）
    handleInput: function() {
      var self = this
      if (!this.realtimeDetectionEnabled || !this.stompClient) return
      
      // 防抖处理，避免频繁发送请求
      clearTimeout(this.debounceTimer)
      this.debounceTimer = setTimeout(function() {
        if (self.inputText.trim() && self.stompClient.connected) {
          self.stompClient.send('/app/detect/realtime', {}, self.inputText)
        }
      }, 500)
    },
    
    // 清空输入
    clearInput: function() {
      this.inputText = ''
      this.detectionResult = {
        originalText: '',
        detectedItems: [],
        totalDetected: 0,
        processingTimeMs: 0
      }
      this.detectionAttempted = false
      this.selectedTypes = []
    },
    
    // 复制结果
    copyResult: function() {
      var self = this
      if (!this.hasResults) return
      
      var resultText = ''
      for (var i = 0; i < this.detectionResult.detectedItems.length; i++) {
        var item = this.detectionResult.detectedItems[i]
        resultText += item.type.name + ': ' + item.content + ' (' + item.type.riskLevel.name + ')'
        if (i < this.detectionResult.detectedItems.length - 1) {
          resultText += '\n'
        }
      }
      
      navigator.clipboard.writeText(resultText).then(function() {
        self.$message.success('结果已复制到剪贴板')
      }).catch(function() {
        self.$message.error('复制失败，请手动复制')
      })
    },
    
    // 切换类型筛选
    toggleTypeFilter: function(typeCode) {
      var index = this.selectedTypes.indexOf(typeCode)
      if (index > -1) {
        this.selectedTypes.splice(index, 1)
      } else {
        this.selectedTypes.push(typeCode)
      }
    },
    
    // 检查类型是否被选中
    isTypeSelected: function(typeCode) {
      return this.selectedTypes.includes(typeCode)
    },
    
    // 获取指定类型的敏感数据数量
    getTypeCount: function(typeCode) {
      var count = 0
      var typeCodeLower = typeCode.toLowerCase()
      
      // 为了更好地处理身份证号统计，我们直接创建一个更灵活的匹配机制
      for (var i = 0; i < this.detectionResult.detectedItems.length; i++) {
        var item = this.detectionResult.detectedItems[i]
        var itemTypeCode = (item.type.code || '').toLowerCase()
        var itemTypeName = (item.type.name || '')
        var itemContent = item.content || ''
        
        // 匹配条件1: 直接匹配type.code
        var matchByCode = itemTypeCode === typeCodeLower
        
        // 匹配条件2: 针对身份证特殊处理
        var isIdCardItem = itemTypeName === '身份证号' || 
                         itemTypeCode.includes('idcard') || 
                         itemTypeCode.includes('identity')
        var isIdCardFilter = typeCodeLower.includes('idcard') || 
                           typeCodeLower.includes('identity') ||
                           typeCodeLower.includes('身份证')
        
        // 匹配条件3: 通过sensitiveDataTypes中的映射关系进行匹配
        var matchByTypeName = false
        for (var j = 0; j < this.sensitiveDataTypes.length; j++) {
          var type = this.sensitiveDataTypes[j]
          if (type.code === typeCode && type.name === itemTypeName) {
            matchByTypeName = true
            break
          }
        }
        
        // 匹配条件4: 特殊处理18位数字，将其同时计入身份证号统计
        // 这是因为后端可能将某些18位数字识别为银行卡号
        var is18DigitNumber = /^\d{18}$/.test(itemContent)
        var countAsIdCard = is18DigitNumber && isIdCardFilter
        
        // 满足任一匹配条件即可计数
        if (matchByCode || (isIdCardItem && isIdCardFilter) || matchByTypeName || countAsIdCard) {
          count++
        }
      }
      
      return count
    },
    
    // 获取标签类型（基于风险等级）
    getTagType: function(riskLevel) {
      switch (riskLevel) {
        case '高风险':
          return 'danger'
        case '中风险':
          return 'warning'
        case '低风险':
          return 'success'
        default:
          return 'info'
      }
    },
    
    // 获取标签颜色（基于风险等级）
    getTagColor: function(riskLevel) {
      switch (riskLevel) {
        case '高风险':
          return '#ff4d4f'
        case '中风险':
          return '#faad14'
        case '低风险':
          return '#52c41a'
        default:
          return '#1890ff'
      }
    },
    
    // 获取文本片段的CSS类
    getSegmentClass: function(segment) {
      return segment.isSensitive ? 'sensitive-segment' : 'normal-segment'
    },
    
    // 获取文本片段的样式
    getSegmentStyle: function(segment) {
      if (!segment.isSensitive) return {}
      
      // 确保使用正确的颜色格式
      var color = this.getTagColor(segment.type.riskLevel.name)
      
      return {
        backgroundColor: color + '33', // 添加透明度
        borderBottom: '2px solid ' + color,
        borderRadius: '2px',
        padding: '0 2px'
      }
    },
    
    // 截断文本
    truncateText: function(text, maxLength) {
      if (!text || text.length <= maxLength) return text
      return text.substring(0, maxLength) + '...'
    },
    
    // 获取默认处理建议
    getDefaultSuggestion: function(typeName) {
      var suggestions = {
        '身份证号': '建议使用掩码处理，保留前6位和后4位，中间用*代替',
        '手机号': '建议使用掩码处理，保留前3位和后4位，中间用*代替',
        '银行卡号': '建议使用掩码处理，保留前4位和后4位，中间用*代替',
        '邮箱': '建议使用掩码处理，保留域名和邮箱首字母，其余用*代替',
        '护照': '建议使用掩码处理，保留前2位和后2位，中间用*代替'
      }
      return suggestions[typeName] || '建议根据数据敏感程度采取适当的保护措施'
    }
  }
}
</script>

<style>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.app-header {
  background-color: #1890ff;
  color: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.app-header h1 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 500;
}

.app-main {
  flex: 1;
  display: flex;
  padding: 2rem;
  gap: 2rem;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

.input-section, .result-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.input-section h2, .result-section h2 {
  margin-top: 0;
  margin-bottom: 1rem;
  font-size: 1.4rem;
  color: #333;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 0.5rem;
}

.input-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
}

.result-count {
  font-size: 0.9rem;
  color: #666;
  font-weight: normal;
}

.result-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.filter-tag {
  cursor: pointer;
  user-select: none;
}

.type-badge {
  margin-left: 4px;
}

.result-content {
  flex: 1;
  overflow-y: auto;
}

.highlighted-text {
  white-space: pre-wrap;
  line-height: 1.6;
  padding: 1rem;
  background-color: #f9f9f9;
  border-radius: 4px;
  margin-bottom: 1rem;
  max-height: 300px;
  overflow-y: auto;
}

.sensitive-segment {
  cursor: pointer;
  position: relative;
}

.sensitive-segment:hover {
  filter: brightness(0.95);
}

.sensitive-content {
  font-family: monospace;
  word-break: break-all;
}

.suggestion-text {
  color: #666;
}

.app-footer {
  background-color: #f5f5f5;
  padding: 1rem 2rem;
  text-align: center;
  color: #666;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .app-main {
    flex-direction: column;
    padding: 1rem;
  }
  
  .input-section, .result-section {
    padding: 1rem;
  }
}
</style>