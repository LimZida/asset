import React, { useState, useEffect } from "react";
import { FolderOpenOutlined, FolderOutlined, LoadingOutlined, RedoOutlined } from "@ant-design/icons";
import { Breadcrumb, Layout, Menu, theme } from "antd";
import request from "../instance";

import { Button, Input, Space, Table, Spin } from "antd";
import { render } from "@testing-library/react";

const columns = [
  {
    title: "코드값",
    dataIndex: "hwNo",
  },
  {
    title: "코드명",
    dataIndex: "hwDiv",
    render: (hwDiv) => `${hwDiv.codeName}`,
  },
  {
    title: "비고",
    dataIndex: "hwLocation",
    render: (hwLocation) => `${hwLocation.codeName}`,
  },
  {
    title: "활성화",
    dataIndex: "hwMfr",
    render: (hwMfr) => `${hwMfr.codeName}`,
  },
];

function addToParent(parentArray, child, parentCode) {
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
}

function reqMenuData(data) {
  const result = [];

  for (let item of data) {
    if (item.codeDepth === 0) {
      item.key = item.code;
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
        item.label = item.codeName + " " + item.codeInnerType;
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
}

// let testData = [
//   {
//     code: "CTG01",
//     codeName: "전체",
//     codeInnerType: "-",
//     upperCode: "-",
//     codeType: "CTG",
//     codeDepth: 0,
//     activeYn: true,
//   },
//   {
//     code: "CTG0101",
//     codeName: "공통",
//     codeInnerType: "-",
//     upperCode: "CTG01",
//     codeType: "CTG",
//     codeDepth: 1,
//     activeYn: true,
//   },
//   {
//     code: "CTG010101",
//     codeName: "부서",
//     codeInnerType: "(DPT)",
//     upperCode: "CTG0101",
//     codeType: "CTG",
//     codeDepth: 2,
//     activeYn: true,
//   },
//   {
//     code: "CTG010102",
//     codeName: "위치",
//     codeInnerType: "(LOC)",
//     upperCode: "CTG0101",
//     codeType: "CTG",
//     codeDepth: 2,
//     activeYn: true,
//   },
//   {
//     code: "CTG010103",
//     codeName: "상태명",
//     codeInnerType: "(STS)",
//     upperCode: "CTG0101",
//     codeType: "CTG",
//     codeDepth: 2,
//     activeYn: true,
//   },
//   {
//     code: "CTG010104",
//     codeName: "자산 종류",
//     codeInnerType: "(TYPE)",
//     upperCode: "CTG0101",
//     codeType: "CTG",
//     codeDepth: 2,
//     activeYn: true,
//   },
//   {
//     code: "CTG010105",
//     codeName: "자산 용도",
//     codeInnerType: "(USE)",
//     upperCode: "CTG0101",
//     codeType: "CTG",
//     codeDepth: 2,
//     activeYn: true,
//   },
//   {
//     code: "CTG0102",
//     codeName: "하드웨어",
//     codeInnerType: "-",
//     upperCode: "CTG01",
//     codeType: "CTG",
//     codeDepth: 1,
//     activeYn: true,
//   },
//   {
//     code: "CTG010201",
//     codeName: "자산명",
//     codeInnerType: "(AST)",
//     upperCode: "CTG0102",
//     codeType: "CTG",
//     codeDepth: 2,
//     activeYn: true,
//   },
//   {
//     code: "CTG010202",
//     codeName: "제조사",
//     codeInnerType: "(COM)",
//     upperCode: "CTG0102",
//     codeType: "CTG",
//     codeDepth: 2,
//     activeYn: true,
//   },
//   {
//     code: "CTG010203",
//     codeName: "CPU",
//     codeInnerType: "(CPU)",
//     upperCode: "CTG0102",
//     codeType: "CTG",
//     codeDepth: 2,
//     activeYn: true,
//   },
//   {
//     code: "CTG0103",
//     codeName: "소프트웨어",
//     codeInnerType: "-",
//     upperCode: "CTG01",
//     codeType: "CTG",
//     codeDepth: 1,
//     activeYn: true,
//   },
//   {
//     code: "CTG010301",
//     codeName: "자산명",
//     codeInnerType: "(AST)",
//     upperCode: "CTG0103",
//     codeType: "CTG",
//     codeDepth: 2,
//     activeYn: true,
//   },
//   {
//     code: "CTG010302",
//     codeName: "제조사",
//     codeInnerType: "(COM)",
//     upperCode: "CTG0103",
//     codeType: "CTG",
//     codeDepth: 2,
//     activeYn: true,
//   },
//   {
//     code: "CTG010303",
//     codeName: "프로그램 및 운영체제",
//     codeInnerType: "(OS)",
//     upperCode: "CTG0103",
//     codeType: "CTG",
//     codeDepth: 2,
//     activeYn: true,
//   },
//   {
//     code: "CTG010304",
//     codeName: "운영체제 버전",
//     codeInnerType: "(OSV)",
//     upperCode: "CTG0103",
//     codeType: "CTG",
//     codeDepth: 2,
//     activeYn: true,
//   },
//   {
//     code: "CTG010305",
//     codeName: "라이센스 유형",
//     codeInnerType: "(LIC)",
//     upperCode: "CTG0103",
//     codeType: "CTG",
//     codeDepth: 2,
//     activeYn: true,
//   },
// ];

const Codes = () => {
  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;
  const [menuList, setMenuList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchList, setSearchList] = useState();
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const { Content, Sider } = Layout;

  useEffect(() => {
    test();
  }, []);

  const test = function () {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements/categorys";
    setLoading(true);

    request
      .get(url)
      .then((res) => {
        console.log("res", res);

        let menuData = reqMenuData(res.data);
        // let menuData = reqMenuData(testData);
        setMenuList(menuData);
        setLoading(false);
      })
      .catch((res) => {
        //실패
        console.log("실패");
        setLoading(false);
      });
  };

  return (
    <Layout>
      <Layout loading={loading ? { indicator: <Spin indicator={antIcon} /> } : false}>
        <Sider width={300} style={{ background: colorBgContainer }}>
          <Menu mode="inline" defaultSelectedKeys={["1"]} defaultOpenKeys={["sub1"]} style={{ height: "100%", borderRight: 0 }} items={menuList} />
        </Sider>
        <Layout style={{ padding: "0 24px 24px" }}>
          <Breadcrumb style={{ margin: "16px 0" }}>
            <Breadcrumb.Item>Home</Breadcrumb.Item>
          </Breadcrumb>
          <Content
            style={{
              padding: 24,
              margin: 0,
              minHeight: 280,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            <Table
              loading={loading ? { indicator: <Spin indicator={antIcon} /> } : false}
              columns={columns}
              dataSource={searchList}
              onRow={(record, rowIndex) => {
                return {
                  onClick: (event) => {
                    console.log(record);
                  }, // click row
                };
              }}
            />
          </Content>
        </Layout>
      </Layout>
    </Layout>
  );
};

export default Codes;
