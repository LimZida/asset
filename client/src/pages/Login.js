import { useEffect } from "react";
import "../styles/Login.css";
import axios from "axios";
import { useLocation, useNavigate } from "react-router-dom";
import { Card } from "antd";
import { useCookies } from "react-cookie";
import { useDispatch, useSelector } from "react-redux";
import {changeUserInfo} from "../store/store";

function Login() {
  let state = useSelector((state)=> state);
  let dispatch = useDispatch();//store.js로 요청보내주는 함수

  let navigate = useNavigate();
  let location = useLocation();
  const [cookies, setCookie, removeCookie] = useCookies(['id']);

  useEffect(() => {
    console.log(state);
    if(location.pathname === "/"){
      navigate("/login"); //링크 고정
    }else{
      let saveId = localStorage.getItem("userId");
      if (saveId) {
        document.getElementById("userId").value = saveId;
      }
    }
     // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  function ReqLogin(userId, userPw) { //로그인
    let url = "http://218.55.79.25:8087/mcnc-mgmts/auth/login";
    let data = {
      userId: userId,
      userPw: userPw,
    };
  
    axios
      .post(url, data)
      .then((res) => {
        //성공
        console.log("성공");
        if (document.getElementById("remember-check").checked) {
          localStorage.setItem("userId", userId);
          dispatch(changeUserInfo(userId));
        }else{
          localStorage.removeItem("userId");
        }

        localStorage.setItem("authorization", res.headers.authorization);
        navigate("/main");
      })
      .catch((res) => {
        //실패
        console.log("실패");
        console.log(res);
      });
  }

  return (
    <>
      <div className="login-wrapper">
        <Card className="loginWrap">
          <h2>MCNC 자산 관리 시스템</h2>
          <div className="contentsWrap">
            <input type="text" id="userId" placeholder="Id" />
            <input type="password" id="userPw" placeholder="Password" />
            <div>
              <input type="checkbox" name="remember-check"
                id="remember-check"
              />
              <label htmlFor="remember-check">아이디 저장하기</label>
            </div>
            <button
              onClick={() => {
                let id = document.getElementById("userId").value;
                let pw = document.getElementById("userPw").value;
                ReqLogin(id, pw);
              }}
            >
              Login
            </button>
          </div>
        </Card>
      </div>
    </>
  );
}

export default Login;
