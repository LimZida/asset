import React, { useState, useEffect } from "react";
import { LoadingOutlined } from "@ant-design/icons";
import request from "../instance";
import { errHandler } from "../common/utils";
import { Input, Table, Spin, Form, Typography, Popconfirm, Switch, Layout, theme, Select, Button } from "antd";
import EmployeeModal from "../component/EmployeeModal";
const { Content } = Layout;
const { Search } = Input;

const Employee = () => {
  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const [editingKey, setEditingKey] = useState("");
  const isEditing = (record) => record.key === editingKey;
  const [searchList, setSearchList] = useState();
  const [originalArray, setOriginalArray] = useState();
  const [deptList, setDeptList] = useState([]);
  const [searchValue, setSearchValue] = useState("");
  const [selectedRow, setSelectedRow] = useState({});

  useEffect(() => {
    reqEmployeesData();
    reqDepartmentsData();
  }, []);
  useEffect(() => {
    if (searchValue) {
      const matchingArray = originalArray.filter((element) => element.userName.includes(searchValue) || element.deptName.includes(searchValue) || element.userId.includes(searchValue));
      setSearchList(matchingArray);
    } else {
      setSearchList(originalArray);
    }
  }, [searchValue, originalArray]);

  /**
   * @title 전직원 조회
   */
  const reqEmployeesData = async () => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/employee-managements/employees";
    setLoading(true);

    try {
      const res = await request.get(url);
      var searchData = [];
      for (var i = 0; i < res.data.length; i++) {
        searchData.push({ key: i, ...res.data[i] });
      }
      setOriginalArray([...searchData]);
      setSearchList([...searchData]);
    } catch (error) {
      errHandler(error);
    } finally {
      setLoading(false);
    }
  };

  /**
   * @title 부서코드 조회
   */
  const reqDepartmentsData = async () => {
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
    } catch (error) {
      errHandler(error);
    } finally {
      setLoading(false);
    }
  };

  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const edit = (record) => {
    form.setFieldsValue({ userName: "", userId: "", deptName: "", ...record });
    setSelectedRow(record);
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
    } catch (error) {
      errHandler(error);
    } finally {
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
          userDept: row.deptName === selectedRow.deptName ? selectedRow.userDept : row.deptName,
        });

        setEditingKey("");
        reqEmployeesData();
      } catch (error) {
        errHandler(error);
      } finally {
        setLoading(false);
      }
    } catch (error) {
      errHandler(error);
    }
  };
  const columns = [
    {
      title: "이름",
      dataIndex: "userName",
      key: "userName",
      editable: true,
      sorter: (a, b) => {
        return a.userName.localeCompare(b.userName);
      },
    },
    {
      title: "아이디",
      dataIndex: "userId",
      key: "userId",
      sorter: (a, b) => {
        return a.userId.localeCompare(b.userId);
      },
    },
    {
      title: "부서",
      dataIndex: "deptName",
      key: "deptName",
      editable: true,
      sorter: (a, b) => {
        return a.deptName.localeCompare(b.deptName);
      },
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
      sorter: (a, b) => {
        return a.activeYn - b.activeYn;
      },
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

  const [isModalOpen2, setIsModalOpen2] = useState(false);
  const showModal2 = () => {
    setIsModalOpen2(true);
  };

  const closeCodeModal = (data) => {
    setIsModalOpen2(data);
  };

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
            {title === "부서" ? <Select value={record.deptName} options={deptList} /> : <Input />}
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
      <div style={{ width: "100%", marginBottom: 20, display: "flex", justifyContent: "space-between" }}>
        <Search
          style={{ width: "40%" }}
          value={searchValue}
          onChange={(event) => {
            setSearchValue(event.target.value);
          }}
        />
        <Button className="primaryBtn" onClick={showModal2}>
          직원 추가
        </Button>
      </div>
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
      <EmployeeModal showModal={isModalOpen2} closeCodeModal={closeCodeModal} deptList={deptList} updateEmployee={reqEmployeesData} />
    </Content>
  );
};

export default Employee;
