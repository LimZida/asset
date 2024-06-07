import { configureStore, createSlice } from '@reduxjs/toolkit'
// import user from './userSlice';


//state 하나를 slice라고 부름
let user = createSlice({
  name : 'user', //이름
  initialState: { 
    userInfo : {
      userId: "", //아이디
      authorization : "", //토큰
      },
  } , //값
  reducers : {
    changeUserInfo(state, actions){
      state.userInfo.userId = actions.payload;
      console.log("Redux 변경>>>>>>>");
    },
  }
})

export let { changeUserInfo } = user.actions;

export default configureStore({
  reducer: {
    user : user.reducer,
   }
}) 