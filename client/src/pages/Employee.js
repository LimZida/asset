import React, { useState, useEffect } from "react";
import { LoadingOutlined } from "@ant-design/icons";
import request from "../instance";
import { errHandler } from "../common/utils";
import { Input, Table, Spin, Form, Typography, Popconfirm, Switch, Layout, theme, Select } from "antd";
const { Content } = Layout;

const Employee = () => {
  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const [editingKey, setEditingKey] = useState("");
  const isEditing = (record) => record.key === editingKey;
  const [searchList, setSearchList] = useState();
  const [deptList, setDeptList] = useState([]);
  const [sortOrder, setSortOrder] = useState("asc");

  useEffect(() => {
    reqEmployeesData();
    reqDepartmentsData();
  }, []);

  /**
   * @title 전직원 조회
   */
  const reqEmployeesData = async function () {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/employee-managements/employees";
    setLoading(true);

    try {
      const res = await request.get(url);
      var searchData = [];
      for (var i = 0; i < res.data.length; i++) {
        searchData.push({ key: i, ...res.data[i] });
      }
      setSearchList(searchData);
      setLoading(false);
    } catch (error) {
      errHandler(error);
      setLoading(false);
    }
  };

  /**
   * @title 부서코드 조회
   */
  const reqDepartmentsData = async function () {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/employee-managements/departments";
    setLoading(true);

    try {
      const res = await request.get(url);

      let departments = res.data.filter((item) => {
        item.label = item.deptName;
        item.value = item.deptCode;
        return true;
      });
      setDeptList(departments);
      setLoading(false);
    } catch (error) {
      errHandler(error);
      setLoading(false);
    }
  };

  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const edit = (record) => {
    form.setFieldsValue({ userName: "", userId: "", deptName: "", ...record });
    setEditingKey(record.key);
  };

  const cancel = () => {
    setEditingKey("");
  };

  const updateActiveYn = async (record) => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/employee-managements/employees/activation";
    setLoading(true);

    try {
      await request.put(url, {
        userId: record.userId,
      });

      reqEmployeesData();
      setLoading(false);
    } catch (error) {
      errHandler(error);
      setLoading(false);
    }
  };

  /**
   * @title 임직원 수정
   */
  const updateEmployee = async (record) => {
    try {
      const row = await form.validateFields();
      let url = "http://218.55.79.25:8087/mcnc-mgmts/employee-managements/employees";
      setLoading(true);

      try {
        await request.put(url, {
          userId: record.userId,
          userName: row.userName,
          userDept: row.deptName,
        });

        setLoading(false);
        setEditingKey("");
        reqEmployeesData();
      } catch (error) {
        errHandler(error);
        setLoading(false);
      }
    } catch (error) {
      errHandler(error);
      console.log(error);
    }
  };

  const columns = [
    {
      title: "이름",
      dataIndex: "userName",
      key: "userName",
      editable: true,
      sorter: (a, b) => {
        const newSortOrder = sortOrder === "asc" ? "desc" : "asc";
        setSortOrder(newSortOrder);
        if (newSortOrder === "asc") {
          return a.userName.localeCompare(b.userName);
        } else {
          return b.userName.localeCompare(a.userName);
        }
      },
    },
    {
      title: "아이디",
      dataIndex: "userId",
      key: "userName",
      sorter: (a, b) => a.userId - b.userId,
    },
    {
      title: "부서",
      dataIndex: "deptName",
      editable: true,
      sorter: (a, b) => a.deptName - b.deptName,
    },
    {
      title: "활성화",
      dataIndex: "activeYn",
      render: (activeYn, record) => (
        <Switch
          onChange={() => {
            updateActiveYn(record);
          }}
          checked={record.activeYn}
        />
      ),
    },
    {
      title: "",
      dataIndex: "",
      render: (_, record) => {
        const editable = isEditing(record);
        return editable ? (
          <span>
            <Typography.Link
              onClick={() => updateEmployee(record)}
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

  const EditableCell = ({ editing, dataIndex, title, inputType, record, index, children, ...restProps }) => {
    return (
      <td {...restProps}>
        {editing ? (
          <Form.Item
            name={dataIndex}
            style={{ margin: 0 }}
            rules={[
              {
                required: true,
                message: `${title}을 입력해주세요.`,
              },
            ]}
          >
            {title === "부서" ? <Select checked={record.deptName} options={deptList} /> : <Input />}
          </Form.Item>
        ) : (
          children
        )}
      </td>
    );
  };

  return (
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
        />
      </Form>
    </Content>
  );
};

export default Employee;
