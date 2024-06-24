import React, { useState, useEffect } from "react";
import request from "../instance";
import { errHandler } from "../common/utils";
import { LoadingOutlined, CheckOutlined } from "@ant-design/icons";
import { Button, Input, Typography, Modal, Select, Space, Descriptions, Spin } from "antd";
const { Text } = Typography;

const CategoryModal = (props) => {
  const [codeDepth2Data, setCodeDepth2Data] = useState([]);
  const [categoryCode, setCategoryCode] = useState("");
  const [categoryName, setCategoryName] = useState("");
  const [codeType, setCodeType] = useState("");
  const [updateCategoryFlag, setUpdateCategoryFlag] = useState(false);
  const [errText1, setErrText1] = useState(false);
  const [errText2, setErrText2] = useState(false);
  const [errText3, setErrText3] = useState(false);
  const [loading, setLoading] = useState(false);
  const [showCodeType, setShowCodeType] = useState(false);
  const [validateDept1, setValidateDept1] = useState(false);
  const [validateDept2, setValidateDept2] = useState(false);
  const [selectedDept1, setSelectedDept1] = useState({ value: "createCategory", upperCode: "CTG01" });
  const [selectedDept2, setSelectedDept2] = useState({ value: "updateCategory", label: "카테고리 수정" }, { value: "createCategory", label: "카테고리 추가" });
  const [selectedDept1Value, setSelectedDept1Value] = useState("createCategory");
  const [selectedDept2Value, setSelectedDept2Value] = useState("updateCategory");
  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;

  // select init
  let codeDepth1data = props.originMenuData.filter((item) => {
    if ([1].includes(item.codeDepth)) {
      item.label = item.codeName;
      item.value = item.code;
      return true;
    }
    return false;
  });
  codeDepth1data.unshift({ value: "createCategory", label: "카테고리 추가" });

  useEffect(() => {
    setValidateDept1(false);
    if (categoryName === "") {
      setErrText1(true);
    } else {
      setErrText1(false);
    }
  }, [categoryName]);

  useEffect(() => {
    if (codeType === "") {
      setErrText2(true);
      setErrText3(false);
    } else {
      setErrText2(false);
      const regex = /^[A-Z]{2,3}$/;
      if (regex.test(codeType)) {
        setErrText3(false);
      } else {
        setErrText3(true);
      }
    }
  }, [codeType]);

  useEffect(() => {
    if (selectedDept2.value === "createCategory") {
      setShowCodeType(true);
    } else {
      setShowCodeType(false);
    }
  }, [selectedDept2]);

  useEffect(() => {
    initData();
  }, [props.originMenuData]);

  const initData = () => {
    setValidateDept1(false);
    setValidateDept2(false);
    setUpdateCategoryFlag(false);
    setCategoryName("");
    setCategoryCode("");
    setCodeType("");
    setSelectedDept1Value("createCategory");
    setSelectedDept2Value("updateCategory");
    setSelectedDept1({ value: "createCategory", upperCode: "CTG01" });
    setSelectedDept2({ value: "updateCategory", label: "카테고리 수정" });
  };

  // 저장버튼
  const updateData = () => {
    if (validateData()) {
      if (selectedDept1.value === "createCategory") {
        createCategory();
      } else if (selectedDept2.value === "updateCategory") {
        updateCategory();
      } else if (selectedDept2.value === "createCategory") {
        createCategory();
      } else {
        updateCategory();
      }
    }
  };

  const validateData = () => {
    if (!categoryName) {
      Modal.error({
        title: "카테고리명을 입력해주세요.",
      });
      return;
    }
    if (selectedDept1.value === "createCategory") {
      if (!validateDept1) {
        Modal.error({
          title: "카테고리명 중복확인이 필요합니다.",
        });
        return;
      }
    } else if (selectedDept2.value === "createCategory") {
      if (!validateDept1) {
        Modal.error({
          title: "카테고리명 중복확인이 필요합니다.",
        });
        return;
      }
      if (!validateDept2) {
        Modal.error({
          title: "코드타입 중복확인이 필요합니다.",
        });
        return;
      }
      if (!codeType) {
        Modal.error({
          title: "코드타입을 입력해주세요.",
        });
        return;
      }
    }
    return true;
  };

  const closeModal = () => {
    initData();
    props.closeCateModal(false);
  };

  /** 카테고리 관리 셀렉트 이벤트 */
  const handleDept1 = (value, e) => {
    setSelectedDept1Value(value);
    setSelectedDept2Value("updateCategory");
    setShowCodeType(false);
    if (value === "createCategory") {
      setUpdateCategoryFlag(false);
      setCategoryCode("");
      setCategoryName("");
      setSelectedDept1({ value: "createCategory", upperCode: "CTG01" });
      setCodeDepth2Data([]);
    } else {
      setUpdateCategoryFlag(true);
      setCategoryCode(value);
      setCategoryName(e.codeName);
      setSelectedDept1(e);
      let temp = e.children ? [...e.children] : [];
      temp.unshift({ value: "updateCategory", label: "카테고리 수정" }, { value: "createCategory", label: "카테고리 추가" });
      setCodeDepth2Data(temp);
    }
  };

  const handleDept2 = (value, e) => {
    setSelectedDept2Value(value);
    setSelectedDept2(e);
    if (value === "updateCategory") {
      setCategoryName(selectedDept1.codeName);
      setCategoryCode(selectedDept1.code);
    } else {
      if (value === "createCategory") {
        setCategoryCode("");
        setCategoryName("");
      } else {
        setCategoryCode(value);
        setCategoryName(e.codeName);
      }
    }
  };

  /**
   * @title 카테고리 중복검사
   */
  const reqValidations = async () => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements/categorys/validations";
    setLoading(true);

    if (!errText1) {
      try {
        const res = await request.post(url, {
          codeName: categoryName,
          codeType: "CTG",
          upperCode: selectedDept1.value === "createCategory" ? selectedDept1.upperCode : selectedDept1.value,
        });

        if (res.data.code === "") {
          Modal.error({
            content: "중복된 카테고리명입니다.",
          });
        } else {
          Modal.success({
            title: "사용가능한 카테고리명입니다.",
          });
          setValidateDept1(true);
          setCategoryCode(res.data.code);
        }
      } catch (error) {
        errHandler(error);
      } finally {
        setLoading(false);
      }
    }
  };

  /**
   * @title 코드타입 중복검사
   */
  const reqValidationsCodeType = async () => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements/categorys/codetype-validations";
    setLoading(true);

    if (!errText2 && !errText3) {
      try {
        const res = await request.post(url, {
          codeType: codeType,
        });

        if (res.data.result === "dup") {
          Modal.error({
            content: "중복된 코드타입입니다.",
          });
        } else if (res.data.result === "notDup") {
          Modal.success({
            title: "사용가능한 코드타입입니다.",
          });
          setValidateDept2(true);
        }
      } catch (error) {
        errHandler(error);
      } finally {
        setLoading(false);
      }
    }
  };

  /**
   * @title 카테고리 추가
   */
  const createCategory = async () => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements/categorys";
    setLoading(true);

    try {
      await request.post(url, {
        upperCode: selectedDept1.value === "createCategory" ? selectedDept1.upperCode : selectedDept1.value,
        codeDepth: selectedDept1.value === "createCategory" ? "1" : "2",
        codeType: selectedDept1.value === "createCategory" ? "CTG" : codeType,
        codeName: categoryName,
        code: categoryCode,
      });

      Modal.success({
        title: "카테고리가 추가되었습니다.",
      });
      props.updateCategory();
      props.closeCateModal(false);
    } catch (error) {
      errHandler(error);
    } finally {
      setLoading(false);
    }
  };

  /**
   * @title 카테고리 수정
   */
  const updateCategory = async () => {
    let url = "http://218.55.79.25:8087/mcnc-mgmts/code-managements/categorys";
    setLoading(true);

    try {
      await request.put(url, {
        codeName: categoryName,
        code: categoryCode,
      });

      Modal.success({
        title: "카테고리가 수정되었습니다.",
      });
      props.updateCategory();
      props.closeCateModal(false);
    } catch (error) {
      errHandler(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title="카테고리 관리"
      open={props.showModal}
      onOk={updateData}
      onCancel={closeModal}
      okButtonProps={{
        children: "Custom OK",
      }}
      cancelButtonProps={{
        children: "Custom cancel",
      }}
      okText="저장"
      cancelText="닫기"
      loading={loading ? { indicator: <Spin indicator={antIcon} /> } : false}
    >
      <Space wrap>
        <Descriptions column={1} bordered>
          <Descriptions.Item label="Dept1">
            <Select value={selectedDept1Value} defaultValue="카테고리 추가" style={{ width: 300 }} onChange={handleDept1} options={codeDepth1data} />
          </Descriptions.Item>
          {updateCategoryFlag && (
            <Descriptions.Item label="Dept2">
              <Select value={selectedDept2Value} defaultValue="카테고리 수정" style={{ width: 300 }} onChange={handleDept2} options={codeDepth2Data} />
            </Descriptions.Item>
          )}
          <Descriptions.Item label="카테고리값">
            <Input style={{ width: 300 }} value={categoryCode} disabled />
          </Descriptions.Item>
          <Descriptions.Item label="카테고리명">
            <div>
              <Input
                style={{ width: 160 }}
                value={categoryName}
                onChange={(event) => {
                  setCategoryName(event.target.value);
                }}
              />
              {(selectedDept1.value === "createCategory" || selectedDept2.value === "createCategory") && (
                <>
                  <Button style={{ width: 130, marginLeft: 10 }} className="primaryBtn" onClick={reqValidations}>
                    중복확인
                    {validateDept1 && (
                      <span style={{ marginLeft: 10 }}>
                        <CheckOutlined />
                      </span>
                    )}
                  </Button>
                </>
              )}
            </div>
            {errText1 && <Text type="danger">카테고리명은 필수 입력사항입니다.</Text>}
          </Descriptions.Item>
          {showCodeType && (
            <Descriptions.Item label="코드타입(ex: DPT, OSY, SYS)">
              <div>
                <Input
                  style={{ width: 160 }}
                  value={codeType}
                  onChange={(event) => {
                    setCodeType(event.target.value);
                  }}
                />
                <Button style={{ width: 130, marginLeft: 10 }} className="primaryBtn" onClick={reqValidationsCodeType}>
                  중복확인
                  {validateDept2 && (
                    <span style={{ marginLeft: 10 }}>
                      <CheckOutlined />
                    </span>
                  )}
                </Button>
              </div>
              {errText2 && <Text type="danger">코드타입은 필수 입력사항입니다.</Text>}
              {errText3 && <Text type="danger">코드타입은 대문자 영어 2~3자리여야 합니다.</Text>}
            </Descriptions.Item>
          )}
        </Descriptions>
      </Space>
    </Modal>
  );
};

export default CategoryModal;
