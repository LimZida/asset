import { useEffect, useState } from "react";
import "../styles/Login.css";
import axios from "axios";
import { useLocation, useNavigate } from "react-router-dom";
import { Card } from "antd";
import { useCookies } from "react-cookie";
import { useDispatch, useSelector } from "react-redux";
import { changeUserInfo } from "../store/store";
import CryptoJS from "crypto-js";

function Login() {
  let state = useSelector((state) => state);
  let dispatch = useDispatch();

  let navigate = useNavigate();
  let location = useLocation();
  const [cookies, setCookie, removeCookie] = useCookies(["id"]);

  const [userId, setUserId] = useState("skpark2");
  const [userPw, setUserPw] = useState("mcnc1234!@");

  useEffect(() => {
    console.log(state);
    if (location.pathname === "/") {
      navigate("/login");
    } else {
      let saveId = localStorage.getItem("userId");
      if (saveId) {
        setUserId(saveId);
      }
    }
  }, [location.pathname]);

  function encryptAES(text) {
    const key = "01234567890123456789012345678901"; // AES 알고리즘에 사용할 키
    const iv = "0123456789012345"; // AES의 CBC 모드에서 사용할 초기화 벡터(IV)

    let encrypted = CryptoJS.AES.encrypt(text, CryptoJS.enc.Utf8.parse(key), {
      iv: CryptoJS.enc.Utf8.parse(iv),
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7,
    });
    return encrypted.toString();
  }

  function decryptAES(ciphertext) {
    const key = "01234567890123456789012345678901"; // AES 알고리즘에 사용할 키
    const iv = "0123456789012345"; // AES의 CBC 모드에서 사용할 초기화 벡터(IV)

    let decrypted = CryptoJS.AES.decrypt(ciphertext, CryptoJS.enc.Utf8.parse(key), {
      iv: CryptoJS.enc.Utf8.parse(iv),
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7,
    });
    return decrypted.toString(CryptoJS.enc.Utf8);
  }

  function ReqLogin() {
    let url = "http://218.55.79.57:8087/mcnc-mgmts/auth/login";
    let encryptedPw = encryptAES(userPw); // 비밀번호를 AES로 암호화

    let data = {
      userId: userId,
      userPw: encryptedPw, // 암호화된 비밀번호 전송
    };

    axios
      .post(url, data)
      .then((res) => {
        console.log("로그인 성공");
        if (document.getElementById("remember-check").checked) {
          localStorage.setItem("userId", userId);
          dispatch(changeUserInfo(userId));
        } else {
          localStorage.removeItem("userId");
        }

        localStorage.setItem("authorization", res.headers.authorization);
        navigate("/main");
      })
      .catch((error) => {
        console.log("로그인 실패", error);
      });
  }

  return (
    <>
      <div className="login-wrapper">
        <Card className="loginWrap">
          <h2>MCNC 자산 관리 시스템</h2>
          <div className="contentsWrap">
            <input type="text" id="userId" placeholder="Id" value={userId} onChange={(e) => setUserId(e.target.value)} />
            <input type="password" id="userPw" placeholder="Password" value={userPw} onChange={(e) => setUserPw(e.target.value)} />
            <div>
              <input type="checkbox" name="remember-check" id="remember-check" />
              <label htmlFor="remember-check">아이디 저장하기</label>
            </div>
            <button onClick={() => ReqLogin()}>Login</button>
          </div>
        </Card>
      </div>
    </>
  );
}

export default Login;
