import React, { useState, useEffect } from "react";
import { FolderOpenOutlined, FolderOutlined, LoadingOutlined } from "@ant-design/icons";
import { Button, Input, Table, Spin, Form, Typography, Popconfirm, Switch, Breadcrumb, Layout, Menu, theme } from "antd";
import request from "../instance";
import CategoryModal from "../component/CategoryModal";
import CodeModal from "../component/CodeModal";

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

function MenuDataSetting(data) {
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
}

const EditableCell = ({ editing, dataIndex, title, inputType, record, index, children, ...restProps }) => {
  const inputNode = <Input />;

  return (
    <td {...restProps}>
      {editing ? (
        <Form.Item
          name={dataIndex}
          style={{ margin: 0 }}
          rules={[
            {
              required: true,
              message: `Please Input ${title}!`,
            },
          ]}
        >
          {inputNode}
        </Form.Item>
      ) : (
        children
      )}
    </td>
  );
};

const Codes = () => {
  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;
  const [menuList, setMenuList] = useState([]);
  const [originMenuData, setOriginMenuData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState();
  const [searchList, setSearchList] = useState();
  const [form] = Form.useForm();
  const [editingKey, setEditingKey] = useState("");
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  const isEditing = (record) => record.key === editingKey;

  const edit = (record) => {
    form.setFieldsValue({ code: "", codeName: "", codeRemark: "", ...record });
    setEditingKey(record.key);
  };

  const cancel = () => {
    setEditingKey("");
  };

  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const { Content, Sider } = Layout;

  useEffect(() => {
    reqMenuData();
  }, []);

  /**
   * @title 메뉴 조회
   */
  const reqMenuData = function () {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements/categorys";
    setLoading(true);

    request
      .get(url)
      .then((res) => {
        setOriginMenuData(res.data);
        let menuData = MenuDataSetting([...res.data]);
        setMenuList(menuData);
        setLoading(false);
      })
      .catch((res) => {
        //실패
        console.log("실패");
        setLoading(false);
      });
  };

  /**
   * @title 코드 조회
   */
  const reqCodeData = function (code) {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements";
    setLoading(true);

    request
      .get(url, {
        params: {
          code: code,
        },
      })
      .then((res) => {
        var searchData = [];
        for (var i = 0; i < res.data.codeList.length; i++) {
          searchData.push({ key: i, ...res.data.codeList[i] });
        }
        setSearchList(searchData);
        setLoading(false);
      })
      .catch((res) => {
        //실패
        console.log("실패");
        setLoading(false);
      });
  };

  const onChange2 = (checked) => {
    console.log("checked", checked);
  };

  const setSelectedMenu = (keyPath) => {
    let selected = originMenuData.filter((item) => keyPath.includes(item.code));
    setSelectedRowKeys(selected);
  };

  const save = async (key) => {
    try {
      const row = await form.validateFields();
      const newData = [...data];
      const index = newData.findIndex((item) => key === item.key);
      if (index > -1) {
        const item = newData[index];
        newData.splice(index, 1, {
          ...item,
          ...row,
        });
        setData(newData);
        setEditingKey("");
      } else {
        newData.push(row);
        setData(newData);
        setEditingKey("");
      }
    } catch (errInfo) {
      console.log("Validate Failed:", errInfo);
    }
  };

  const columns = [
    {
      title: "코드값",
      dataIndex: "code",
      editable: true,
    },
    {
      title: "코드명",
      dataIndex: "codeName",
      editable: true,
    },
    {
      title: "비고",
      dataIndex: "codeRemark",
      editable: true,
    },
    {
      title: "활성화",
      dataIndex: "activeYn",
      render: (activeYn, record) => <Switch onChange={() => onChange2(record)} defaultChecked={activeYn} />,
    },
    {
      title: "",
      dataIndex: "",
      render: (_, record) => {
        const editable = isEditing(record);
        return editable ? (
          <span>
            <Typography.Link
              onClick={() => save(record.key)}
              style={{
                marginRight: 8,
              }}
            >
              저장
            </Typography.Link>
            <Popconfirm title="취소하시겠습니까?" onConfirm={cancel}>
              <a>취소</a>
            </Popconfirm>
          </span>
        ) : (
          <Typography.Link disabled={editingKey !== ""} onClick={() => edit(record)}>
            수정
          </Typography.Link>
        );
      },
    },
  ];
  const mergedColumns = columns.map((col) => {
    if (!col.editable) {
      return col;
    }
    return {
      ...col,
      onCell: (record) => ({
        record,
        inputType: "text",
        dataIndex: col.dataIndex,
        title: col.title,
        editing: isEditing(record),
      }),
    };
  });

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
  codeDepth1data.unshift({ value: "categoryAdd", label: "카테고리 추가" });

  const closeCateModal = (data) => {
    setIsModalOpen(data);
  };

  const closeCodeModal = (data) => {
    setIsModalOpen2(data);
  };

  const updateCategory = () => {
    reqMenuData();
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
            defaultOpenKeys={["CTG01", "CTG0101", "CTG0102", "CTG0103"]}
            style={{ height: `calc( 100% - 32px )`, borderRight: 0 }}
            items={menuList}
            onClick={function ({ item, key, keyPath, domEvent }) {
              setSelectedMenu(keyPath);
              reqCodeData(key);
            }}
          />
        </Sider>
        <Layout style={{ padding: "0 24px 24px" }}>
          <Breadcrumb style={{ margin: "16px 0" }}>
            <Button style={{ width: 90, marginRight: 10 }} className="primaryBtn" onClick={showModal2}>
              코드추가
            </Button>
            {selectedRowKeys.map((selected, idx) => (
              <Breadcrumb.Item key={idx}>{selected.codeName}</Breadcrumb.Item>
            ))}
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
            <Form form={form} component={false}>
              <Table
                loading={loading ? { indicator: <Spin indicator={antIcon} /> } : false}
                components={{
                  body: {
                    cell: EditableCell,
                  },
                }}
                bordered
                rowClassName="editable-row"
                pagination={{
                  onChange: cancel,
                }}
                columns={mergedColumns}
                dataSource={searchList}
                // onRow={(record, rowIndex) => {
                //   return {
                //     onClick: (event) => {
                //       console.log(record);
                //     }, // click row
                //   };
                // }}
              />
            </Form>
          </Content>
        </Layout>
      </Layout>
      <CategoryModal showModal={isModalOpen} originMenuData={originMenuData} closeCateModal={closeCateModal} updateCategory={updateCategory} />
      <CodeModal showModal={isModalOpen2} closeCodeModal={closeCodeModal} />
    </Layout>
  );
};

export default Codes;
