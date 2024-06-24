import React, { useState, useEffect, useRef } from "react";
import { LoadingOutlined } from "@ant-design/icons";
import { errHandler } from "../common/utils.js";
import request from "../instance.js";
import DoughnutChart from "../component/DoughnutChart.js";
import { Table, Spin } from "antd";

function Dashboard() {
  const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;
  const [loading, setLoading] = useState(true);
  const [hwData, setHwData] = useState([]);
  const [swData, setSwData] = useState([]);
  const [analyticsData, setAnalyticsData] = useState([]);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const chartRef = useRef();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      await Promise.all([reqHwData(), reqSwData(), reqAnalyticsData()]);
    } catch (error) {
      errHandler(error);
    } finally {
      setLoading(false);
    }
  };

  const reqHwData = async () => {
    let url = "http://218.55.79.57:8087/mcnc-mgmts/dashboards/analytics/hardware-details";
    const res = await request.get(url);
    const searchData = res.data.map((item, i) => ({
      key: i,
      ...item,
      label: item.assetCodeName,
      value: item.assetCode,
    }));
    setHwData(searchData);
  };

  const reqSwData = async () => {
    let url = "http://218.55.79.57:8087/mcnc-mgmts/dashboards/analytics/software-details";
    const res = await request.get(url);
    const searchData = res.data.map((item, i) => ({
      key: i,
      ...item,
      label: item.assetCodeName,
      value: item.assetCode,
    }));
    setSwData(searchData);
  };

  const reqAnalyticsData = async () => {
    let url = "http://218.55.79.57:8087/mcnc-mgmts/dashboards/analytics/in-outs?rowCount=10";
    const res = await request.get(url);
    const searchData = res.data.map((item, i) => ({
      key: i,
      ...item,
      label: item.assetDiv,
      value: item.assetNo,
    }));
    setAnalyticsData(searchData);
  };

  const columns = [
    {
      title: "자산번호",
      dataIndex: "assetNo",
      key: "assetNo",
    },
    {
      title: "자산구분",
      dataIndex: "assetDiv",
      key: "assetDiv",
      sorter: (a, b) => a.assetDiv.localeCompare(b.assetDiv),
    },
    {
      title: "자산명",
      dataIndex: "assetName",
      key: "assetName",
      sorter: (a, b) => a.assetName.localeCompare(b.assetName),
    },
    {
      title: "이전 사용자",
      dataIndex: "prevUserName",
      key: "prevUserName",
      sorter: (a, b) => a.prevUserName.localeCompare(b.prevUserName),
    },
    {
      title: "이후 사용자",
      dataIndex: "newUserName",
      key: "newUserName",
      sorter: (a, b) => a.newUserName.localeCompare(b.newUserName),
    },
    {
      title: "자산상태",
      dataIndex: "assetStatus",
      key: "assetStatus",
      sorter: (a, b) => a.assetStatus.localeCompare(b.assetStatus),
    },
    {
      title: "최종변경일",
      dataIndex: "lastModifiedDate",
      key: "lastModifiedDate",
      sorter: (a, b) => a.lastModifiedDate.localeCompare(b.lastModifiedDate),
    },
  ];

  const onRowMouseEnter = (record) => {
    if (chartRef.current) {
      chartRef.current.setState("selected", (datum) => datum.assetCodeName === record.assetCodeName);
    }
    setSelectedRowKeys([record.assetCodeName]);
  };

  const onRowMouseLeave = () => {
    if (chartRef.current) {
      chartRef.current.setState("selected", () => true, false);
    }
    setSelectedRowKeys([]);
  };

  return (
    <>
      {loading ? (
        <Spin indicator={antIcon} style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "400px" }} />
      ) : (
        <>
          <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 50, overflowX: "auto" }}>
            <div style={{ flex: "0 0 48%" }}>
              <DoughnutChart data={hwData} type={"하드웨어"} />
            </div>
            <div style={{ flex: "0 0 48%" }}>
              <DoughnutChart data={swData} type={"소프트웨어"} />
            </div>
          </div>
          <Table
            columns={columns}
            dataSource={analyticsData}
            rowKey="assetCodeName"
            onRow={(record) => ({
              onMouseEnter: () => onRowMouseEnter(record),
              onMouseLeave: onRowMouseLeave,
            })}
            pagination={false}
          />
        </>
      )}
    </>
  );
}

export default Dashboard;
