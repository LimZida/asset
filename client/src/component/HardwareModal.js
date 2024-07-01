import "../styles/Hardwares.css"
import React, { useState, useEffect } from "react";
import { Button, Input, Modal, Space, Descriptions, Tabs, Spin } from "antd";
import { LoadingOutlined, CheckOutlined } from "@ant-design/icons"
import request from "../instance";
import { errHandler } from "../common/utils";
import HardwareModalData from "../antItems/HardwareModalData"
import items from './../antItems/HardwareModalData';
import { editableInputTypes } from "@testing-library/user-event/dist/utils";

const HardwareModal = (props) => {
  const [loading, setLoading] = useState(false);
  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;
  console.log(props)
  const onChange = (key) => {
    console.log(key);
  };

  let idx = 0;
  let items = [];
  if(Object.keys(props.selected).length > 0){
    items = [
      {
        key: String(idx),
        label: '자산번호',
        editableInputTypes : true,
        children: props.selected.hwNo,
      },
      {
        key: String(++idx),
        label: '자산구분',
        children: props.selected.hwDiv.codeName,
      },
      {
        key: String(++idx),
        label: '위치',
        children: props.selected.hwLocation.codeName,
      },
      {
        key: String(++idx),
        label: 'S/N',
        children: props.selected.hwSN,
      },
      {
        key: String(++idx),
        label: '제조사',
        children: props.selected.hwMfr.codeName,
      },
      {
        key: String(++idx),
        label: '모델명',
        children: props.selected.hwModel,
      },
      {
        key: String(++idx),
        label: 'CPU',
        // span: 4,
        children: props.selected.hwCpu.codeName,
      },
      {
        key: String(++idx),
        label: '메모리',
        itemPaddingBottom : 0,
        span: 2,
        children : <Descriptions className="DescriptionsContents" style={{padding: 0}} layout="vertical" bordered items={[{
          key: '1',
          label: 'Product',
          children: 'Cloud Database',
        },
        {
          key: '2',
          label: 'Billing Mode',
          children: 'Prepaid',
        }]} />
        // children: props.selected.hwRam1 > 0 ? props.selected.hwRam1 : "-",
      },
      {
        key: String(++idx),
        label: 'SSD1',
        children: props.selected.hwSsd1 > 0 ? props.selected.hwSsd1 : "-",
      },
      {
        key: String(++idx),
        label: 'SSD2',
        children: props.selected.hwSsd2 > 0 ? props.selected.hwSsd2 : "-",
      },
      {
        key: String(++idx),
        label: 'HDD1',
        children: props.selected.hwHdd1  > 0 ? props.selected.hwHdd1 : "-",
      },
      {
        key: String(++idx),
        label: 'HDD2',
        children: props.selected.hwHdd2 > 0 ? props.selected.hwHdd2 : "-",
      },
      {
        key: String(++idx),
        label: '구매일',
        children: props.selected.purchaseDate,
      },
      {
        key: String(++idx),
        label: '자산상태',
        children: props.selected.usageInfo.assetStatus.codeName,
      },
      {
        key: String(++idx),
        label: '현사용자',
        children: props.selected.usageInfo.usageDept.usageName,
      },
      // {
      //   key: String(++idx),
      //   label: '전사용자',
      //   children: props.selected.usageInfo.usageDept.usageName,
      // },
      // {
      //   key: String(++idx),
      //   label: '지급일',
      //   children: '지급일',
      // },
      // {
      //   key: String(++idx),
      //   label: '반납일',
      //   children: '반납일',
      // },
      {
        key: String(++idx),
        label: '사용기간',
        children: '사용기간',
      },
      {
        key: String(++idx),
        label: '최종변경일',
        children: props.selected.updateDate,
      },
    ];
  }

  const detailContents = () => {
    return (
      <>
       <Descriptions column={6} layout="vertical" bordered items={items} />;
      </>
    );
  }

  const tabItems = [
    {
      label: `상세`,
      key: 0,
      children: detailContents()
    },
    {
      label: `히스토리`,
      key: 1,
      children: `히스토리`,
    }
  ];

  const initData = () => {
  };

  const handleOk = () => {
    // if (!validateDept) {
    //   Modal.error({
    //     title: "코드명 중복확인이 필요합니다.",
    //   });
    //   return;
    // }
    createCode();
  };

  const handleCancel = () => {
    // initData();
    // props.closeCodeModal(false);
  };

  /**
   * @title 코드 추가
   */
  const createCode = async () => {
    // let url = "http://218.55.79.57:8087/mcnc-mgmts/code-managements";
    // setLoading(true);

    try {
      // await request.post(url, {
      //   codeCtg: props.selected.value,
      //   code: code,
      //   codeName: codeName,
      //   codeType: props.selected.codeInnerType.replace(/[()]/g, ""),
      //   codeRemark: codeRemark,
      // });

      // props.updateCategory();
      // Modal.success({
      //   title: "코드가 추가되었습니다.",
      // });
      // props.updateCode(props.selected.value);
      // props.closeCodeModal(false);
    } catch (error) {
      // errHandler(error);
    } finally {
      // setLoading(false);
    }
  };

  return (
    <Modal
      open={props.showModal}
      loading={loading ? { indicator: <Spin indicator={antIcon} /> } : false}
    >
    <Tabs
        style={{paddingTop : 30}}
        onChange={onChange}
        type="card"
        items={tabItems}
      />
    </Modal>
  );
};

export default HardwareModal;
