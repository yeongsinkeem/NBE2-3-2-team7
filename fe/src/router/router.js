import { createRouter, createWebHistory } from 'vue-router';

const routes = [
	{ path: '/test', component: () => import('../views/HelloWorld.vue') },
	{ path: '/', component: () => import('../views/AppHome.vue') },
	{ path: '/land', component: () => import('../views/LandList.vue') },
	{ path: '/land/:id', component: () => import('../views/LandView.vue') },
	{ path: '/popup', component: () => import('../views/PopupList.vue') },
	{ path: '/popup/:id', component: () => import('../views/PopupView.vue') },
	// { path: '/payment', component: () => import('../views/AppPayment.vue') },
	//   { path: '/user', component: () => import('../views/UserView.vue') },
	//   { path: '/', component: () => import('../views/UserView.vue') },

];

const router = createRouter({
	history: createWebHistory(),
	routes
});

export default router;