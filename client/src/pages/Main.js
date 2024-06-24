import "../styles/Main.css";
import Hardwares from "./Hardwares.js";
import Softwares from "./Softwares.js";
import Codes from "./Codes.js";
import Employee from "./Employee.js";
import History from "./History.js";
import Dashboard from "./Dashboard.js";
import { Routes, Route, Link } from "react-router-dom";
import React, { useState } from "react";
import { HddOutlined, HistoryOutlined, LayoutOutlined, SettingOutlined, UserOutlined, LaptopOutlined } from "@ant-design/icons";
import { Breadcrumb, Layout, Menu, theme } from "antd";
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
const items = [getItem(<Link to="">메인화면</Link>, "1", <LayoutOutlined />), getItem("MENU", "sub1", <UserOutlined />, [getItem(<Link to="hardwares">하드웨어</Link>, "2", <HddOutlined />), getItem(<Link to="softwares">소프트웨어</Link>, "3", <LaptopOutlined />)], "group"), getItem("SETTING", "sub2", <UserOutlined />, [getItem(<Link to="codes">코드관리</Link>, "4", <HddOutlined />), getItem(<Link to="employee">직원관리</Link>, "5", <SettingOutlined />), getItem(<Link to="history">히스토리</Link>, "6", <HistoryOutlined />)], "group")];

function Main() {
  let state = useSelector((state) => state);

  const [collapsed, setCollapsed] = useState(false);
  const [selectedMenu, setSelectedMenu] = useState({ key: 1, name: items.find((val) => val.key === "1").label });

  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();
  return (
    <Layout
      style={{
        minHeight: "100vh",
      }}
    >
      <Sider theme="light" collapsible collapsed={collapsed} onCollapse={(value) => setCollapsed(value)}>
        <div className="demo-logo-vertical" />
        <p className="menu-title">자산관리</p>
        <Menu
          theme="light"
          defaultSelectedKeys={["1"]}
          mode="inline"
          items={items}
          onClick={(e) => {
            setSelectedMenu({ key: e.key, name: e.domEvent.target.innerText });
          }}
        />
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
            margin: "0 16px",
          }}
        >
          <BreadcrumbMenu selectedMenu={selectedMenu}> </BreadcrumbMenu>

          <div
            style={{
              padding: 24,
              minHeight: 360,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/hardwares" element={<Hardwares />} />
              <Route path="/softwares" element={<Softwares />} />
              <Route path="/codes" element={<Codes />} />
              <Route path="/employee" element={<Employee />} />
              <Route path="/history" element={<History />} />
            </Routes>
          </div>
        </Content>
        <Footer
          style={{
            textAlign: "center",
          }}
        ></Footer>
      </Layout>
    </Layout>
  );
}

const BreadcrumbMenu = function (selectedMenu) {
  let sKey = selectedMenu.selectedMenu.key;
  let sName = selectedMenu.selectedMenu.name;

  var sub1Arr = items.filter((val) => val.key === "sub1");
  var sub2Arr = items.filter((val) => val.key === "sub2");

  return (
    <>
      {sKey === "1" ? (
        <Breadcrumb
          style={{
            margin: "16px 0",
          }}
        ></Breadcrumb>
      ) : (
        <Breadcrumb
          style={{
            margin: "16px 0",
          }}
        >
          <Breadcrumb.Item>{sKey === "2" || sKey === "3" ? sub1Arr[0].label : sub2Arr[0].label}</Breadcrumb.Item>
          <Breadcrumb.Item>{sName}</Breadcrumb.Item>
        </Breadcrumb>
      )}
    </>
  );
};
export default Main;
