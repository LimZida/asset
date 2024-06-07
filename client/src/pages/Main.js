import "../styles/Main.css";
import Hardwares from "./Hardwares.js";
import { Routes, Route, Link } from "react-router-dom";
import React, { useState } from 'react';
import {
HddOutlined,
HistoryOutlined,
  LayoutOutlined,
  SettingOutlined,
  UserOutlined,
  LaptopOutlined,
} from '@ant-design/icons';
import { Breadcrumb, Layout, Menu, theme } from 'antd';
import { useDispatch, useSelector } from "react-redux";


const { Header, Content, Footer, Sider } = Layout;

function getItem(label, key, icon, children, type) {
    return {
      key,
      icon,
      children,
      label,
      type,
    };
  }
  const items = [
    getItem('메인화면', '1', <LayoutOutlined />),
    getItem('MENU', 'sub1', <UserOutlined />, [
      getItem(<Link to="hardwares">하드웨어</Link>, '2', <HddOutlined />),
      getItem('소프트웨어', '3', <LaptopOutlined />),
    ],'group'),
    getItem('SETTING', 'sub2', <UserOutlined />, [
        getItem('코드관리', '4', <HddOutlined />),
        getItem('직원관리', '5', <SettingOutlined />),
        getItem('히스토리', '6', <HistoryOutlined />),
      ],'group'),
  ];

function Main() {
  let state = useSelector((state)=> state);

  const [collapsed, setCollapsed] = useState(false);
  const [selectedMenu, setSelectedMenu] = useState(0);

  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();
  return (
    <Layout
      style={{
        minHeight: '100vh',
      }}
    >
      <Sider theme="light" collapsible collapsed={collapsed} onCollapse={(value) => setCollapsed(value)}>
        <div className="demo-logo-vertical" />
        <p className="menu-title">자산관리</p>
        <Menu theme="light" defaultSelectedKeys={['1']} mode="inline" items={items} onClick={(e) => {
          console.log(e.domEvent.target.value);
     }}/>
      </Sider>
      <Layout>
        <Header
          style={{
            padding: 0,
            background: colorBgContainer,
          }}
        />
        <Content
          style={{
            margin: '0 16px',
          }}
        >
          <Breadcrumb 
            style={{
              margin: '16px 0',
            }}
          >
            <Breadcrumb.Item>ㅇ</Breadcrumb.Item>
            <Breadcrumb.Item>ㅇ</Breadcrumb.Item>
          </Breadcrumb>
          <div
            style={{
              padding: 24,
              minHeight: 360,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            Bill is a cat.
            <Routes>
              <Route path="/hardwares" element={<Hardwares />} />
            </Routes>
          </div>
        </Content>
        <Footer
          style={{
            textAlign: 'center',
          }}
        >
        </Footer>
      </Layout>
    </Layout>
  );
}

export default Main;