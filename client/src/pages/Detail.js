/* eslint-disable */ //warning 없애기
import "../../src/App.css";
import { useContext, useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
// import styled from "styled-components";
import { Nav } from "react-bootstrap";
import { Context1 } from "../../src/App.js";
import { changeList } from "../../src/store.js";
import { useSelector, useDispatch } from "react-redux";

function Detail(props) {
  let cartData = useSelector((state) => state);
  let dispatch = useDispatch();
  let navigate = useNavigate();

  let {stock, shoes} = useContext(Context1);

  let [alertYN, setAlertYN] = useState(true); 
  let [count, setCount] = useState(0);
  let [inpVal, setInpVal] = useState("");
  
  let {id} = useParams();
  let shoesOne = props.shoes.find((val) => val.id == id);

  //최근 조회한 상품 localStorage
  useEffect(()=>{
    let temp = localStorage.getItem('watched');
    if(temp){
      var arr = JSON.parse(temp)
    }else{
      var arr = [];
    }
    arr.push(shoesOne.id);
    arr = new Set(arr);
    arr = Array.from(arr);
    localStorage.setItem('watched', JSON.stringify(arr));
  }, []);


  let [tab, setTab] = useState(0);
  let [fade, setFade] = useState('');

  useEffect(()=>{ //mount, update시 코드 실행해주는 useEffect (근데 useEffect에 굳이 안넣어도 똑같음)
    //다른점. useEffect 안에 있는 코드는 html 렌더링 후에 동작함
    //side Effect 코드(함수의 핵심기능과 상관없는 부가기능) 보관함
    let timer = setTimeout(() => { setAlertYN(false); }, 2000); 

    return () => { //useEffect 동작 전에 실행됨
      clearTimeout(timer);
      //clean up function은 mount시 실행 안됨,
      //unmount시 실행됨
    }
  }, [count]); //[]를 넣으면 []안에 state가 변할때마다 실행됨.(mount될때, 혹은 []안 state가 update될때)
              // []안에 아무것도 없으면 mount될때만 실행됨.

  useEffect(()=>{
    if(isNaN(inpVal)){
      alert("숫자만 입력가능합니다!");
      setInpVal("");
    }
  }, [inpVal]);

  useEffect(()=>{ //mount될때 애니메이션 실행
    let timer2 = setTimeout(() => { setFade('end') });

    return ()=>{
      clearTimeout(timer2);
      setFade('');
    }
  }, []);
  
  return (
    <div className={`container start ${fade}`}>
      {
        alertYN == true ? 
          <div className="alert alert-warning">
          2초이내 구매시 할인
          </div> 
          : null
      }
      {/* {count}
      <button onClick={()=>{ setCount(count+1)}}>버튼</button> */}
      <div className="row">
        <div className="col-md-6">
          <img
            src={"https://codingapple1.github.io/shop/shoes" + (shoesOne.id + 1) + ".jpg"}
            alt="detailImg"
            width="100%"
          />
        </div>
        <div style={{textAlign:"center"}}>
          {/* <input type="tel" style={{width:"100px", textAlign:"center"}}
            onChange={(e)=>{ setInpVal(e.target.value);
          }}></input> */}
        </div>
        <div className="col-md-6">
          <h4 className="pt-5">{shoesOne.title}</h4>
          <p>{shoesOne.content}</p>
          <p>{shoesOne.price}원</p>
          <button className="btn btn-danger" onClick={() => {
            console.log(shoesOne);
            var obj = {id : shoesOne.id , name : shoesOne.title, count : 1};
            dispatch( changeList(obj));
            navigate("/cart");
          }}>주문하기</button>
        </div>
      </div>
      <br/>
      {/* defaultActiveKey 기본으로 눌려있을 버튼 */}
      <Nav variant="tabs"  defaultActiveKey="link0"> 
        <Nav.Item>
          <Nav.Link eventKey="link0" onClick={() => { setTab(0);}}>버튼0</Nav.Link>
        </Nav.Item>
        <Nav.Item>
          <Nav.Link eventKey="link1" onClick={() => { setTab(1);}}>버튼1</Nav.Link>
        </Nav.Item>
        <Nav.Item>
          <Nav.Link eventKey="link2" onClick={() => { setTab(2);}}>버튼2</Nav.Link>
        </Nav.Item>
      </Nav>
      <TabContent tab={tab} shoes={props.shoes}/>

    </div>
  );
}

// props.~ 가 귀찮으면 {변수}
function TabContent({tab, shoes}){
  let [fade, setFade] = useState('');
  let {stock} = useContext(Context1);

  useEffect(() => {
    let timer = setTimeout(()=>{ setFade('end') }, 500);
    
    return ()=>{
      clearTimeout(timer);
      setFade('');
    }
  }, [tab])
  // if(tab == 0){
  //   return <div>내용0</div>
  // }else if(tab == 1){
  //   return <div>내용1</div>
  // }else if(tab == 2){
  //   return <div>내용2</div>
  // }
  //굳이 if문 안써도 됨. 아래로 축약
  var arr = [<div>{shoes[0].title}</div>, <div>내용1</div>, <div>내용2</div>];

  return (<div className={`start ${fade}`}>
      { arr[tab] }
    </div>);
}

export default Detail;
