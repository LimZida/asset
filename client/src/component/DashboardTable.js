import React, { useRef, useEffect, useState } from "react";
import { Pie } from "@antv/g2plot";
import { Table } from "antd";
import insertCss from "insert-css";

insertCss(`
  .hover-g2plot-pie {
    cursor: pointer;
    margin-bottom: 2px;
  }
  .hover-g2plot-pie:hover {
    background-color: #EEF7FF;
  }
`);

const DoughnutChart = (props) => {
  const [data, setData] = useState([]);
  const [updatedData, setUpdatedData] = useState([]);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const chartContainerRef = useRef();
  const chartRef = useRef();

  const totalSum = props.data.reduce((sum, item) => sum + item.totalCnt, 0);
  useEffect(() => {
    if (props.data.length > 0) {
      const newData = props.data.map((item) => ({
        ...item,
        percentage: totalSum === 0 ? 0 : ((item.totalCnt / totalSum) * 100).toFixed(1), // 비율 계산
      }));
      newData.sort((a, b) => b.percentage - a.percentage);
      setData(newData);
      setUpdatedData(newData); // updatedData 상태에 설정
    }
  }, [props.data, totalSum]);

  useEffect(() => {
    if (chartRef.current) {
      chartRef.current.update({ data });
    } else if (chartContainerRef.current) {
      chartRef.current = new Pie(chartContainerRef.current, {
        data,
        angleField: "totalCnt",
        colorField: "assetCodeName",
        padding: 20,
        interactions: [
          {
            type: "element-selected",
          },
          {
            type: "element-active",
          },
        ],
      });
      chartRef.current.render();
      chartRef.current.on("element:click", (evt) => {
        const { data: clickData } = evt;
        const selectedKey = clickData.data.assetCodeName;
        const selectedRow = updatedData.find((item) => item.assetCodeName === selectedKey);
        if (selectedRow) {
          setSelectedRowKeys([selectedRow.assetCodeName]);
        }
      });
    }
  }, [data, updatedData]);

  const columns = [
    {
      title: props.type + "명",
      dataIndex: "assetCodeName",
      key: "assetCodeName",
    },
    {
      title: "총자산",
      dataIndex: "totalCnt",
      key: "totalCnt",
      sorter: (a, b) => {
        return b.totalCnt - a.totalCnt;
      },
    },
    {
      title: "할당",
      dataIndex: "allocationAsset",
      key: "allocationAsset",
      sorter: (a, b) => {
        return b.allocationAsset - a.allocationAsset;
      },
    },
    {
      title: "잉여",
      dataIndex: "notAllocationAsset",
      key: "notAllocationAsset",
      sorter: (a, b) => {
        return b.notAllocationAsset - a.notAllocationAsset;
      },
    },
  ];
  if (props.type === "하드웨어") {
    columns.push(
      {
        title: "고장",
        dataIndex: "failureAsset",
        key: "failureAsset",
        sorter: (a, b) => {
          return b.failureAsset - a.failureAsset;
        },
      },
      {
        title: "노후",
        dataIndex: "oldAsset",
        key: "oldAsset",
        sorter: (a, b) => {
          return b.oldAsset - a.oldAsset;
        },
      }
    );
  } else {
    columns.push(
      {
        title: "만료임박",
        dataIndex: "soonExpirationAsset",
        key: "soonExpirationAsset",
        sorter: (a, b) => {
          return b.soonExpirationAsset - a.soonExpirationAsset;
        },
      },
      {
        title: "만료",
        dataIndex: "expirationAsset",
        key: "expirationAsset",
        sorter: (a, b) => {
          return b.expirationAsset - a.expirationAsset;
        },
      }
    );
  }

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
    <Table
      columns={columns}
      dataSource={updatedData}
      rowKey="assetCodeName"
      onRow={(record) => ({
        onMouseEnter: () => onRowMouseEnter(record),
        onMouseLeave: onRowMouseLeave,
      })}
      pagination={false}
    />
  );
};

export default DoughnutChart;
