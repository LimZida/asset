import React, { useState, useEffect } from "react";
import { FolderOpenOutlined, FolderOutlined, LoadingOutlined } from "@ant-design/icons";
import { Button, Spin, Breadcrumb, Layout, Menu, theme } from "antd";
import request from "../instance";
import CategoryModal from "../component/CategoryModal";
import CodeModal from "../component/CodeModal";
import CodeTable from "../component/CodeTable";
import { errHandler } from "../common/utils";

const addToParent = (parentArray, child, parentCode) => {
  for (let parent of parentArray) {
    if (parent.code === parentCode) {
      parent.children = parent.children || [];
      parent.children.push(child);
      return true;
    }
    if (parent.children) {
      if (addToParent(parent.children, child, parentCode)) {
        return true;
      }
    }
  }
  return false;
};

const MenuDataSetting = (data) => {
  const result = [];
  for (let item of data) {
    if (item.codeDepth === 0) {
      item.key = item.code;
      item.value = item.code;
      item.label = item.codeName;
      item.icon = [FolderOpenOutlined].map((icon) => {
        return React.createElement(icon);
      });
      result.push(item);
    }
  }

  for (let item of data) {
    if (item.codeDepth === 1) {
      item.key = item.code;
      item.value = item.code;
      item.label = item.codeName;
      item.icon = [FolderOpenOutlined].map((icon) => {
        return React.createElement(icon);
      });
      addToParent(result, item, item.upperCode);
    }
  }

  for (let item of data) {
    if (item.codeDepth === 2) {
      for (let parent of result) {
        item.key = item.code;
        item.value = item.code;
        item.label = item.codeName;
        item.icon = [FolderOutlined].map((icon) => {
          return React.createElement(icon);
        });
        if (addToParent(parent.children || [], item, item.upperCode)) {
          break;
        }
      }
    }
  }

  return result;
};

const Codes = () => {
  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;
  const [menuList, setMenuList] = useState([]);
  const [originMenuData, setOriginMenuData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchList, setSearchList] = useState();
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [openMenuList, setOpenMenuList] = useState([]);

  const {
    token: { colorBgContainer },
  } = theme.useToken();

  const { Sider } = Layout;

  useEffect(() => {
    reqMenuData();
  }, []);

  /**
   * @title 메뉴 조회
   */
  const reqMenuData = async () => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements/categorys";
    setLoading(true);

    try {
      const res = await request.get(url);
      setOriginMenuData(res.data);
      let menuData = MenuDataSetting([...res.data]);
      setMenuList(menuData);
      const result = [...res.data].filter((item) => item.codeDepth === 0 || item.codeDepth === 1).map((item) => item.code);
      setOpenMenuList(result);
      setLoading(false);
    } catch (error) {
      errHandler(error);
      setLoading(false);
    }
  };

  /**
   * @title 코드 조회
   */
  const reqCodeData = async (code) => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements";
    setLoading(true);

    try {
      const res = await request.get(url, {
        params: {
          code: code,
        },
      });
      var searchData = [];
      for (var i = 0; i < res.data.codeList.length; i++) {
        searchData.push({ key: i, ...res.data.codeList[i] });
      }
      setSearchList(searchData);
      setLoading(false);
    } catch (error) {
      errHandler(error);
      setLoading(false);
    }
  };

  const setSelectedMenu = (keyPath) => {
    let selected = originMenuData.filter((item) => keyPath.includes(item.code));
    setSelectedRowKeys(selected);
  };

  const [isModalOpen, setIsModalOpen] = useState(false);
  const showModal = () => {
    setIsModalOpen(true);
  };
  const [isModalOpen2, setIsModalOpen2] = useState(false);
  const showModal2 = () => {
    setIsModalOpen2(true);
  };

  let codeDepth1data = originMenuData.filter((item) => {
    if ([1].includes(item.codeDepth)) {
      item.label = item.codeName;
      item.value = item.code;
      return true;
    }
    return false;
  });
  codeDepth1data.unshift({ value: "createCategory", label: "카테고리 추가" });

  const closeCateModal = (data) => {
    setIsModalOpen(data);
  };

  const closeCodeModal = (data) => {
    setIsModalOpen2(data);
  };

  const updateCategory = () => {
    reqMenuData();
  };

  const updateCode = (code) => {
    reqCodeData(code);
  };

  return (
    <Layout>
      <Layout loading={loading ? { indicator: <Spin indicator={antIcon} /> } : false}>
        <Sider width={260} style={{ background: colorBgContainer }}>
          <Button style={{ height: "32px", width: "95%" }} className="primaryBtn" onClick={showModal}>
            카테고리 관리
          </Button>
          <Menu
            mode="inline"
            // defaultOpenKeys={openMenuList}
            openKeys={openMenuList}
            onOpenChange={(keys) => setOpenMenuList(keys)}
            style={{ height: `calc( 100% - 32px )`, borderRight: 0 }}
            items={menuList}
            onClick={({ item, key, keyPath, domEvent }) => {
              setSelectedMenu(keyPath);
              reqCodeData(key);
            }}
          />
        </Sider>
        <Layout style={{ padding: "0 24px 24px" }}>
          <div style={{ width: "100%", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <Breadcrumb style={{ margin: "16px 0" }}>
              {selectedRowKeys.map((selected, idx) => (
                <Breadcrumb.Item key={idx}>{selected.codeName}</Breadcrumb.Item>
              ))}
            </Breadcrumb>
            {selectedRowKeys[0] && (
              <Button className="primaryBtn" onClick={showModal2}>
                코드 추가
              </Button>
            )}
          </div>
          <CodeTable searchList={searchList} updateCategory={updateCategory} updateCode={updateCode}></CodeTable>
        </Layout>
      </Layout>
      <CategoryModal showModal={isModalOpen} originMenuData={originMenuData} closeCateModal={closeCateModal} updateCategory={updateCategory} />
      <CodeModal showModal={isModalOpen2} selected={selectedRowKeys[selectedRowKeys.length - 1]} closeCodeModal={closeCodeModal} updateCategory={updateCategory} updateCode={updateCode} />
    </Layout>
  );
};

export default Codes;
