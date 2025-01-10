<script setup>
import { ref, onMounted } from 'vue'
import data from '../services/test.api';

defineProps({
	msg: String,
})

// 상태 관리
const count = ref(0);
const apiData = ref(null); // API에서 가져온 데이터를 저장할 변수
const loading = ref(false); // 데이터 로딩 상태
const error = ref(null); // 에러 상태

onMounted(() => {
  fetchApiData(); // API 데이터 호출
});

const fetchApiData = async () => {
	loading.value = true;
	error.value = null;
	try {
		const result = await data.testApi(); // testApi 호출
		apiData.value = result; // 받은 데이터를 apiData에 저장
	} catch (err) {
		error.value = err; // 에러 처리
	} finally {
		loading.value = false; // 로딩 상태 종료
	}
};

</script>

<template>
	<h1>{{ msg }}</h1>

	<div class="card">
		<button type="button" @click="count++">count is {{ count }}</button>
		<p>
			Edit
			<code>components/HelloWorld.vue</code> to test HMR
		</p>
	</div>

	<p>
		Check out
		<a href="https://vuejs.org/guide/quick-start.html#local" target="_blank">create-vue</a>, the official Vue + Vite
		starter
	</p>
	<p>
		Learn more about IDE Support for Vue in the
		<a href="https://vuejs.org/guide/scaling-up/tooling.html#ide-support" target="_blank">Vue Docs Scaling up
			Guide</a>.
	</p>
	<!-- API 호출 결과 출력 -->
	<div v-if="loading">Loading...</div>
	<div v-if="error" class="error">{{ error }}</div>
	<div v-if="apiData">
		<h2>API Data:</h2>
		<pre>{{ apiData }}</pre> <!-- 받은 데이터를 JSON 형태로 출력 -->
	</div>
	<p class="read-the-docs">Click on the Vite and Vue logos to learn more</p>
</template>

<style scoped>
.read-the-docs {
	color: #888;
}
</style>
