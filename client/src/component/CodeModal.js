import React, { useState, useEffect } from "react";
import { Button, Input, Modal, Space, Descriptions } from "antd";

const CategoryModal = (props) => {
  const handleOk = () => {
    props.closeCodeModal(false);
  };

  const handleCancel = () => {
    props.closeCodeModal(false);
  };

  const [categoryCode, setCategoryCode] = useState("");
  const [categoryName, setCategoryName] = useState("");
  const [note, setNote] = useState("");

  return (
    <Modal
      title="카테고리 관리"
      open={props.showModal}
      onOk={handleOk}
      onCancel={handleCancel}
      okButtonProps={{
        children: "Custom OK",
      }}
      cancelButtonProps={{
        children: "Custom cancel",
      }}
      okText="저장"
      cancelText="닫기"
    >
      <Space wrap>
        <Descriptions column={1} bordered>
          <Descriptions.Item label="코드값">
            <Input style={{ width: 300 }} value={categoryCode} disabled />
          </Descriptions.Item>
          <Descriptions.Item label="코드명">
            <div>
              <Input
                style={{ width: 200 }}
                value={categoryName}
                onChange={(event) => {
                  setCategoryName(event.target.value);
                }}
              />
              <Button style={{ width: 90, marginLeft: 10 }} className="primaryBtn">
                중복확인
              </Button>
            </div>
          </Descriptions.Item>
          <Descriptions.Item label="비고">
            <Input style={{ width: 300 }} value={note} onChange={(e) => setNote(e.target.value)} />
          </Descriptions.Item>
        </Descriptions>
      </Space>
    </Modal>
  );
};

export default CategoryModal;
