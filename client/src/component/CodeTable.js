import React, { useState } from "react";
import { LoadingOutlined } from "@ant-design/icons";
import request from "../instance";
import { errHandler } from "../common/utils";
import { Input, Table, Spin, Form, Typography, Popconfirm, Switch, Layout, theme } from "antd";
const { Content } = Layout;

const CodeTable = (props) => {
  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const [editingKey, setEditingKey] = useState("");
  const isEditing = (record) => record.key === editingKey;

  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const edit = (record) => {
    form.setFieldsValue({ code: "", codeName: "", codeRemark: "", ...record });
    setEditingKey(record.key);
  };

  const cancel = () => {
    setEditingKey("");
  };

  const updateActiveYn = async (record) => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements/activation";
    setLoading(true);

    try {
      await request.put(url, {
        code: record.code,
      });

      props.updateCategory();
      props.updateCode(record.codeCtg);
    } catch (error) {
      errHandler(error);
    } finally {
      setLoading(false);
    }
  };

  /**
   * @title 코드 수정
   */
  const updateCode = async (record) => {
    try {
      const row = await form.validateFields();
      let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements";
      setLoading(true);

      try {
        await request.put(url, {
          code: record.code,
          codeName: row.codeName,
          codeRemark: row.codeRemark,
        });

        props.updateCategory();
        setEditingKey("");
        props.updateCode(record.codeCtg);
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
      title: "코드값",
      dataIndex: "code",
      sorter: (a, b) => {
        return a.code.localeCompare(b.code);
      },
    },
    {
      title: "코드명",
      dataIndex: "codeName",
      editable: true,
      sorter: (a, b) => {
        return a.codeName.localeCompare(b.codeName);
      },
    },
    {
      title: "비고",
      dataIndex: "codeRemark",
      editable: true,
      sorter: (a, b) => {
        return a.codeRemark.localeCompare(b.codeRemark);
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
              onClick={() => updateCode(record)}
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
          dataSource={props.searchList}
        />
      </Form>
    </Content>
  );
};

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
              message: `${title}을 입력해주세요.`,
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
export default CodeTable;
