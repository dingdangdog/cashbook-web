<template>
  <div class="login-container">
    <div>
      <v-card-title
        :class="MOD == 'LOCAL' ? 'drag-area' : ''"
        style="display: flex; border-bottom: 1px solid; padding-bottom: 0.5rem"
      >
        <div>
          <v-btn icon>
            <img src="@/assets/images/cashbook.png" height="40" alt="logo" />
          </v-btn>
          Cashbook
        </div>
        <div style="width: 100%; text-align: right" v-if="MOD == 'LOCAL'">
          <v-btn class="no-drag window-actions" icon="mdi-minus" @click="minimize"> </v-btn>
          <v-btn
            class="no-drag window-actions"
            icon="mdi-dock-window"
            @click="maximize"
            v-show="!isMax"
          >
          </v-btn>
          <v-btn
            class="no-drag window-actions"
            icon="mdi-window-maximize"
            @click="maximize"
            v-show="isMax"
          >
          </v-btn>
          <v-btn class="no-drag window-actions" icon="mdi-close" @click="close"> </v-btn>
        </div>
      </v-card-title>
    </div>
    <div class="login-form">
      <!-- set input width -->
      <div class="icon-container">
        <!-- <img src="@/assets/images/cashbook.png" width="60" alt="logo" /> -->
        <h1 style="margin: 0 0.5rem">Cashbook</h1>
      </div>
      <!-- <v-sheet rounded> -->
      <v-card class="login-card">
        <v-form v-model="form" @submit.prevent="onSubmit">
          <v-text-field
            label="账号"
            autocomplete="username"
            placeholder="请输入账号"
            v-model="signInParam.username"
            :readonly="loading"
            :rules="[required]"
            class="mb-2"
            required
          ></v-text-field>

          <v-text-field
            label="密码"
            autocomplete="current-password"
            placeholder="请输入密码"
            v-model="signInParam.password"
            :type="lookPs ? 'text' : 'password'"
            :readonly="loading"
            :rules="[required]"
            required
            :append-icon="lookPs ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append="lookPs = !lookPs"
          ></v-text-field>

          <br />
          <v-btn
            :disabled="!form"
            :loading="loading"
            block
            color="success"
            size="large"
            type="submit"
            variant="elevated"
          >
            登录
          </v-btn>
          <div style="display: flex; justify-content: space-between; align-items: center">
            <v-switch
              color="warning"
              v-model="themeValue"
              @update:modelValue="toggleTheme()"
              hide-details
              inset
            >
              <template v-slot:label>
                <v-icon
                  :icon="themeValue ? 'mdi-emoticon-cool-outline' : 'mdi-weather-night'"
                  :color="themeValue ? 'warning' : 'white'"
                ></v-icon>
              </template>
            </v-switch>
            <v-btn v-show="openRegister" @click="registerDialog = true">注册账号</v-btn>
            <v-btn @click="resetPasswordDialog = true">忘记密码?</v-btn>
          </div>
        </v-form>
      </v-card>
      <v-dialog v-model="resetPasswordDialog" transition="dialog-bottom-transition" width="25rem">
        <template v-slot:default="{ isActive }">
          <v-card style="padding: 1rem">
            <v-card-title> 重置密码 </v-card-title>
            <v-text-field
              label="账号"
              autocomplete="username"
              placeholder="请输入账号"
              variant="outlined"
              v-model="resetFormData.userName"
              :readonly="loading"
              :rules="[required]"
              class="mb-2"
              required
            ></v-text-field>

            <v-text-field
              label="服务密钥"
              autocomplete="server-key"
              placeholder="请输入服务密钥"
              variant="outlined"
              v-model="resetFormData.serverKey"
              :type="lookKey ? 'text' : 'password'"
              :readonly="loading"
              :rules="[required]"
              required
              :append-icon="lookKey ? 'mdi-eye' : 'mdi-eye-off'"
              @click:append="lookKey = !lookKey"
            ></v-text-field>

            <v-card-actions class="justify-end">
              <v-btn text="取消" @click="resetPasswordDialog = false"></v-btn>
              <v-btn text="重置" variant="elevated" color="success" @click="submitReset"></v-btn>
            </v-card-actions>
          </v-card>
        </template>
      </v-dialog>
      <v-dialog
        v-model="registerDialog"
        transition="dialog-bottom-transition"
        style="max-width: 30rem"
      >
        <template v-slot:default="{ isActive }">
          <v-card style="padding: 1rem">
            <v-card-title> 注册用户 </v-card-title>
            <v-text-field
              label="用户名"
              placeholder="请输入用户名"
              variant="outlined"
              v-model="registerUser.name"
              :rules="[required]"
              class="mb-2"
              required
            ></v-text-field>
            <v-text-field
              label="账号"
              placeholder="请输入账号"
              variant="outlined"
              v-model="registerUser.userName"
              :rules="[required]"
              class="mb-2"
              required
            ></v-text-field>

            <v-text-field
              label="密码"
              placeholder="请输入密码"
              variant="outlined"
              v-model="registerUser.password"
              :type="lookKey ? 'text' : 'password'"
              :readonly="loading"
              :rules="[required]"
              required
              :append-icon="lookKey ? 'mdi-eye' : 'mdi-eye-off'"
              @click:append="lookKey = !lookKey"
            ></v-text-field>
            <v-text-field
              label="确认密码"
              placeholder="请再次输入密码"
              variant="outlined"
              v-model="registerUser.againPassword"
              :type="lookKey ? 'text' : 'password'"
              :readonly="loading"
              :rules="[required]"
              required
              :append-icon="lookKey ? 'mdi-eye' : 'mdi-eye-off'"
              @click:append="lookKey = !lookKey"
            ></v-text-field>

            <v-card-actions class="justify-end">
              <v-btn text="取消" @click="registerDialog = false"></v-btn>
              <v-btn text="注册" variant="elevated" color="success" @click="register"></v-btn>
            </v-card-actions>
          </v-card>
        </template>
      </v-dialog>
      <!-- </v-sheet> -->
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { login, registerApi, resetPasswordApi } from '@/api/api.user'
import { getServerInfo } from '@/api/api.server'
import { errorAlert, successAlert } from '@/utils/alert'
import { useTheme } from 'vuetify'
import type { LoginParam, User } from '@/model/user'
import { setUserInfo } from '@/utils/common'
import { MOD } from '@/stores/flag'

const theme = useTheme()
const themeValue = ref(false)
if (theme.global.name.value == 'light') {
  // console.log(theme.global.name.value)
  themeValue.value = true
}
const toggleTheme = () => {
  // console.log(themeValue.value)
  const to = theme.global.name.value == 'light' ? 'dark' : 'light'
  theme.global.name.value = to
  localStorage.setItem('theme', to)
}

const form = ref(false)
const loading = ref(false)
const lookPs = ref(false)
const lookKey = ref(false)
const signInParam = ref<LoginParam>({ username: '', password: '' })

const onSubmit = () => {
  if (!form.value) return
  loading.value = true
  login(true, signInParam.value)
    .then((res) => {
      // console.log('登录成功', res)
      successAlert('登录成功')
      loading.value = false
      setUserInfo(res)

      // 跳转到首页
      // window.location.reload()
    })
    .catch((err) => {
      errorAlert('登录失败:' + err.message)
    })
    .finally(() => {
      // console.log(res)
      loading.value = false
    })
}

const required = (v: any) => {
  return !!v || '必填'
}

const resetPasswordDialog = ref(false)

const resetFormData = ref({
  userName: '',
  serverKey: ''
})
const submitReset = () => {
  resetPasswordApi(resetFormData.value).then((res) => {
    if (res) {
      successAlert('密码重置成功，请登录！')
      // 跳转到首页
      // router.push({ path: '/' })
      resetPasswordDialog.value = false
      resetFormData.value.userName = ''
      resetFormData.value.serverKey = ''
    } else {
      errorAlert('密码重置失败，请重试！')
    }
  })
}

const registerDialog = ref(false)
const registerUser = ref<User>({})

const register = () => {
  if (registerUser.value.password != registerUser.value.againPassword) {
    errorAlert('两次密码不一致')
    return
  }
  //alert('submit!')
  registerApi(registerUser.value)
    .then((res) => {
      successAlert('注册成功，请登录!')
      registerDialog.value = false
      signInParam.value.username = registerUser.value.userName
      signInParam.value.password = registerUser.value.password
      registerUser.value = {}
    })
    .catch((err) => {
      errorAlert('注册失败: ' + err.message)
    })
}

const minimize = () => {
  // @ts-ignore
  window.electron.minimize()
}

// 最大化标志
const isMax = ref(false)
const maximize = () => {
  // @ts-ignore
  window.electron.maximize()
  isMas()
}

const close = () => {
  if (!localStorage.getItem('remember')) {
    localStorage.clear()
  }
  // @ts-ignore
  window.electron.close()
}

// 判断窗口是否最大化
const isMas = () => {
  // @ts-ignore
  window.electron.isMaximized().then((flag: boolean) => {
    isMax.value = flag
  })
}

const openRegister = ref(false)
onMounted(() => {
  if (MOD.value == 'LOCAL') {
    isMas()
  }
  getServerInfo().then((res) => {
    if (res) {
      openRegister.value = res.openRegister == 'true'
    }
  })
})
</script>

<style scoped>
.login-container {
  height: 100vh;
}
.login-form {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.icon-container {
  margin-top: -10rem;
  margin-bottom: 1rem;
  min-width: 15rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.window-actions {
  color: rgba(19, 116, 33);
}
.window-actions:hover {
  color: rgba(182, 6, 6);
}

.login-card {
  margin: 1rem auto !important;
  padding: 1rem;
  max-width: 30rem;
  width: 100%;
}
</style>
