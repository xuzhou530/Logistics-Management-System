/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.global.struts.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.global.dao.BaseDao;
import com.global.dao.CarDao;
import com.global.dao.DeliverySpotDao;
import com.global.dao.OrderDao;
import com.global.dao.ReceiptDao;
import com.global.dao.ReceiptDetaileDao;
import com.global.struts.form.OrderForm;
import com.global.vo.Car;
import com.global.vo.Carstate;
import com.global.vo.Deliveryspot;
import com.global.vo.Employee;
import com.global.vo.Order;
import com.global.vo.Orderstate;
import com.global.vo.Receipt;
import com.global.vo.Receiptdetaile;
import com.global.vo.User;

/**
 * MyEclipse Struts Creation date: 11-24-2008
 * 
 * XDoclet definition:
 * 
 * @struts.action path="/order" name="orderForm"
 *                input="/ordersManage/Ordering.jsp" scope="request"
 *                validate="true"
 */
public class OrderAction extends BaseDispatchAction {
	/*
	 * Generated Methods
	 */

	/**
	 * Method execute
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	
//	��֤�����Ƿ����
	public ActionForward checkCarNo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String carNo = null;
		try {
			carNo = new String(request.getParameter("carNo").getBytes("ISO-8859-1"),"gbk");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ReceiptDao dao = (ReceiptDao) super.getBean("ReceiptDao");
		boolean b = dao.checkCarNo(carNo);
		PrintWriter out = null;

		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(b){
			
		}else{
			out.write("�ó��Ų�����!��������ȷ���ţ�");
		}
		return null;
	}
	
//	��֤�������Ƿ����
	public ActionForward checkOrderNo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String orderNo = request.getParameter("orderNo").trim();
		
		ReceiptDao dao = (ReceiptDao) super.getBean("ReceiptDao");
		boolean b = dao.checkOrderNo(orderNo);
		PrintWriter out = null;

		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(b){
			
		}else{
			out.write("�ö����Ų�����!��������ȷ�����ţ�");
		}
		return null;
	}
	
	
//	ǩ�ն�����������״̬��Ϊ���
	public ActionForward qianShouOrders(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String[] orders = request.getParameterValues("select_orders");
		BaseDao dao = (BaseDao) super.getBean("BaseDao");
		OrderDao odao = (OrderDao) super.getBean("OrderDao");
		List l = new ArrayList();
		
		for (int i = 0; i < orders.length; i++) {
			Order order = odao.queryOrderByOrderNO(orders[i].trim());
			Orderstate os = (Orderstate) dao.get(Orderstate.class, new Integer(4));
			order.setOrderstate(os);
			dao.update(order);
			
			l.add(order);
		}
		request.setAttribute("OrderList", l);
		
		return mapping.findForward("showQianShouOrders");

	}
	
	//���ｻ�ӣ�����������
	public ActionForward handleOrders(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		User u = (User) session.getAttribute("UserSession");
		
		String[] orders = request.getParameterValues("select_orders");
		String carno = request.getParameter("carNo").trim();
		
		//���³���״̬Ϊ��;��
		ReceiptDao rdao = (ReceiptDao) super.getBean("ReceiptDao");
		rdao.updateCarState(carno);
		
		//String fromName = request.getParameter("fromName");
		//String toName = request.getParameter("toName");
		String remark = request.getParameter("remark");
		String emp = request.getParameter("emp");
		
		BaseDao dao = (BaseDao) super.getBean("BaseDao");
		
		Employee employee = (Employee) dao.get(Employee.class, new Integer(emp));
		
		String createTime = dao.getCurrentTimeFromDB();
		
		String receiptNo = "J" + u.getDeliveryspot().getDeliveryspotid().toString() + System.currentTimeMillis();
		//BaseDao dao = (BaseDao) super.getBean("BaseDao");
		//String receiptNo = dao.getCurrentDateFromDB2()+ System.currentTimeMillis();
		
		String rtype = request.getParameter("outOrIn");
		
		Receipt r = new Receipt();
		r.setCreatetime(createTime);
		r.setDeliveryspot(u.getDeliveryspot());
		r.setCarno(carno);
		r.setReceiptno(receiptNo);
		r.setEmployee(employee);
		//r.setFromname(fromName);
		//r.setToname(toName);
		r.setRemark(remark);
		r.setFlag(0);
		r.setRtype(rtype);
		
		if(rtype.equals("xieChe")){
			CarDao cardao = (CarDao) super.getBean("CarDao");
			List list = cardao.queryCarByCarNO(carno);
			if(list.size()>0){
				Car car = (Car) list.get(0);
				BaseDao basedao = (BaseDao) super.getBean("BaseDao");
				Carstate cs = (Carstate) basedao.get(Carstate.class, 4);
				car.setCarstate(cs);
				cardao.updateCar(car);
			}
		}
		
		dao.save(r);
		
		OrderDao odao = (OrderDao) super.getBean("OrderDao");
		
		for (int i = 0; i < orders.length; i++) {
			Order order = odao.queryOrderByOrderNO(orders[i].trim());
			Orderstate os = (Orderstate) dao.get(Orderstate.class, new Integer(2));
			order.setOrderstate(os);
			dao.update(order);
			
			Receiptdetaile rd = new Receiptdetaile();
			rd.setOder(order);
			rd.setReceipt(r);
			
			dao.save(rd);
		}
		
		request.setAttribute("Receipt", r);
		
		return mapping.findForward("showReceipt");

	}
	//�ý��ӵ��Ų�ѯ���ӵ�
	public ActionForward selectByReceiptNo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String receiptNo = request.getParameter("ReceiptNo").trim();
		ReceiptDao dao = (ReceiptDao) super.getBean("ReceiptDao");
		Receipt r = dao.queryReceiptByReceiptNo(receiptNo);
		
		if(r == null){
			request.setAttribute("ErrorMsg", "�ý��ӵ������ڣ����������룡");
			return mapping.findForward("selectJiaoJieDan");
		}else{
			request.setAttribute("flag", "1");
			request.setAttribute("Receipt", r);
			return mapping.findForward("selectJiaoJieDan");
		}

	}
	//�����͵�ID��ѯ�������н��ӵ�
	public ActionForward selectByDeliverySpot(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String DeliverySpotID = request.getParameter("DeliverySpotID");
		ReceiptDao rdao = (ReceiptDao) super.getBean("ReceiptDao");
		//Set l = rdao.queryReceiptByDeliverySpotID(Integer.parseInt(DeliverySpotID));
		
		String offset = request.getParameter("pager.offset");
	     int intOffset = 0;
	     
	     if(offset != null){
	    	 intOffset = new Integer(offset).intValue();
	     }
	     int count  = rdao.countAllReceiptByDeliverySpotID(Integer.parseInt(DeliverySpotID));
	     
	     List l = rdao.queryReceiptByDeliverySpotID2(Integer.parseInt(DeliverySpotID), intOffset, 10);
	     request.setAttribute("count", count);
	     request.setAttribute("DeliverySpotID", DeliverySpotID);
		 request.setAttribute("flag", "2");
		 
		 request.setAttribute("ReceiptList", l);
		
		return mapping.findForward("selectJiaoJieDan");

	}
	
//	��Ͷ�ݵ��Ų�ѯͶ�ݵ�
	public ActionForward selectTouDiDanByReceiptNo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String receiptNo = request.getParameter("ReceiptNo").trim();
		ReceiptDao dao = (ReceiptDao) super.getBean("ReceiptDao");
		Receipt r = dao.queryReceiptByReceiptNo2(receiptNo);
		
		if(r == null){
			request.setAttribute("ErrorMsg", "��Ͷ�ݵ������ڣ����������룡");
			return mapping.findForward("selectTouDiDan");
		}else{
			request.setAttribute("flag", "1");
			request.setAttribute("Receipt", r);
			return mapping.findForward("selectTouDiDan");
		}

	}
	
	//	�����͵�ID��ѯ��������Ͷ�ݵ�
	public ActionForward selectTouDiDanByDeliverySpot(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String DeliverySpotID = request.getParameter("DeliverySpotID");
		ReceiptDao rdao = (ReceiptDao) super.getBean("ReceiptDao");
		//Set l = rdao.queryReceiptByDeliverySpotID(Integer.parseInt(DeliverySpotID));
		
		String offset = request.getParameter("pager.offset");
	     int intOffset = 0;
	     
	     if(offset != null){
	    	 intOffset = new Integer(offset).intValue();
	     }
	     int count  = rdao.countAllTouDiDanByDeliverySpotID(Integer.parseInt(DeliverySpotID));
	     
	     List l = rdao.queryTouDiDanByDeliverySpotID(Integer.parseInt(DeliverySpotID), intOffset, 10);
	     request.setAttribute("count", count);
	     request.setAttribute("DeliverySpotID", DeliverySpotID);
		 request.setAttribute("flag", "2");
		 
		 request.setAttribute("TouDiDanList", l);
		
		return mapping.findForward("selectTouDiDan");

	}
	
	//����Ͷ�ݣ�����Ͷ�ݣ�
	public ActionForward handleTouDiDan(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		User u = (User) session.getAttribute("UserSession");
		
		String[] orders = request.getParameterValues("select_orders");
		String remark = request.getParameter("remark");
		String emp = request.getParameter("emp");
		
		BaseDao dao = (BaseDao) super.getBean("BaseDao");
		
		Employee employee = (Employee) dao.get(Employee.class, new Integer(emp));
		
		String createTime = dao.getCurrentTimeFromDB();
		
		String receiptNo = "T" + u.getDeliveryspot().getDeliveryspotid().toString() + System.currentTimeMillis();
		
		Receipt r = new Receipt();
		r.setCreatetime(createTime);
		r.setDeliveryspot(u.getDeliveryspot());
		r.setReceiptno(receiptNo);
		r.setEmployee(employee);
		r.setRemark(remark);
		r.setFlag(1);
		
		dao.save(r);
		
		OrderDao odao = (OrderDao) super.getBean("OrderDao");
		
		for (int i = 0; i < orders.length; i++) {
			Order order = odao.queryOrderByOrderNO(orders[i].trim());
			Orderstate os = (Orderstate) dao.get(Orderstate.class, new Integer(3));
			order.setOrderstate(os);
			dao.update(order);
			
			Receiptdetaile rd = new Receiptdetaile();
			rd.setOder(order);
			rd.setReceipt(r);
			
			dao.save(rd);
		}
		
		request.setAttribute("Receipt", r);
		
		return mapping.findForward("showTouDiDan");

	}
	
	//���ӵ���ϸ
	public ActionForward getReceiptDetaile(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String ReceiptID = request.getParameter("ReceiptID");
		String receiptno = request.getParameter("receiptno");
		
		String offset = request.getParameter("pager.offset");
	     int intOffset = 0;
	     
	     if(offset != null){
	    	 intOffset = new Integer(offset).intValue();
	     }
		
		ReceiptDetaileDao dao = (ReceiptDetaileDao) super.getBean("ReceiptDetaileDao");
		//Set set = dao.queryReceiptDetaileByReceiptID(Integer.parseInt(ReceiptID));
		
		int count = dao.countReceiptDetaileByReceiptID(Integer.parseInt(ReceiptID));
		List l = dao.queryReceiptDetaileByReceiptID2(Integer.parseInt(ReceiptID), intOffset, 10);
		
		request.setAttribute("receiptno", receiptno);
		
		request.setAttribute("count", count);
		
		request.setAttribute("ReceiptDetaileList", l);
		
		request.setAttribute("ReceiptID", ReceiptID);
		
		return mapping.findForward("ViewReceiptDetaile");

	}
	
//	Ͷ�ݵ���ϸ
	public ActionForward getTouDiDanDetaile(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String ReceiptID = request.getParameter("ReceiptID");
		String receiptno = request.getParameter("receiptno");
		
		String offset = request.getParameter("pager.offset");
	     int intOffset = 0;
	     
	     if(offset != null){
	    	 intOffset = new Integer(offset).intValue();
	     }
		
		ReceiptDetaileDao dao = (ReceiptDetaileDao) super.getBean("ReceiptDetaileDao");
		//Set set = dao.queryReceiptDetaileByReceiptID(Integer.parseInt(ReceiptID));
		
		int count = dao.countReceiptDetaileByReceiptID(Integer.parseInt(ReceiptID));
		List l = dao.queryReceiptDetaileByReceiptID2(Integer.parseInt(ReceiptID), intOffset, 10);
		
		request.setAttribute("receiptno", receiptno);
		
		request.setAttribute("count", count);
		
		request.setAttribute("ReceiptDetaileList", l);
		
		request.setAttribute("ReceiptID", ReceiptID);
		
		return mapping.findForward("ViewTouDiDanDetaile");

	}
	
	//��ȡͶ��Ա�б�
	public ActionForward getTouDiYuanList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ReceiptDao dao = (ReceiptDao) super.getBean("ReceiptDao");
		HttpSession session = request.getSession();
		User u = (User) session.getAttribute("UserSession");
		
		List l = dao.queryEmployeeByDIDandPosition(u.getDeliveryspot().getDeliveryspotid(), 5);
		
		request.setAttribute("TouDiYuanList", l);
		return mapping.findForward("toudidan");

	}
	
	//	��ȡ����Ա�б�
	public ActionForward getJiaoJieYuanList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ReceiptDao dao = (ReceiptDao) super.getBean("ReceiptDao");
		HttpSession session = request.getSession();
		User u = (User) session.getAttribute("UserSession");
		
		List l = dao.queryEmployeeByDIDandPosition(u.getDeliveryspot().getDeliveryspotid(), 6);
		request.setAttribute("JiaoJieYuanList", l);
		return mapping.findForward("doReceipt");

	}
	
    //����Ͷ��
	public ActionForward createTouDiDan(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		User u = (User) session.getAttribute("UserSession");
		
		String[] orders = request.getParameterValues("select_orders");
		String remark = request.getParameter("remark");
		String emp = request.getParameter("emp");
		
		BaseDao dao = (BaseDao) super.getBean("BaseDao");
		
		Employee employee = (Employee) dao.get(Employee.class, new Integer(emp));
		
		String createTime = dao.getCurrentTimeFromDB();
		
		String receiptNo = u.getDeliveryspot().getDeliveryspotid().toString() + System.currentTimeMillis();
		
		Receipt r = new Receipt();
		r.setCreatetime(createTime);
		r.setDeliveryspot(u.getDeliveryspot());
		r.setReceiptno(receiptNo);
		r.setEmployee(employee);
		r.setRemark(remark);
		
		dao.save(r);
		
		OrderDao odao = (OrderDao) super.getBean("OrderDao");
		
		for (int i = 0; i < orders.length; i++) {
			Order order = odao.queryOrderByOrderNO(orders[i]);
			Orderstate os = (Orderstate) dao.get(Orderstate.class, new Integer(2));
			order.setOrderstate(os);
			dao.update(order);
			
			Receiptdetaile rd = new Receiptdetaile();
			rd.setOder(order);
			rd.setReceipt(r);
			
			dao.save(rd);
		}
		
		request.setAttribute("Receipt", r);
		
		return mapping.findForward("showReceipt");

	}
	/* ���涩������ */
	public ActionForward addOrder(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		OrderForm orderForm = (OrderForm) form;
		/* ��ȡ�ռ������� */
		String receiveName = orderForm.getReceiveName();
		/* ��ȡ�ռ��˵�ַ */
		String receiveAddress = orderForm.getReceiveAddress();
		/* ��ȡ�ռ��˹̶��绰 */
		String receiveTel = orderForm.getReceiveTel();
		/* ��ȡ�ռ����ƶ��绰 */
		String receiveMobile = orderForm.getReceiveMobile();
		/* ��ȡ�ռ����ʱ� */
		String receivePostCode = orderForm.getReceivePostCode();
		/* ��ȡ�ļ������� */
		String sendName = orderForm.getSendName();
		/* ��ȡ�ļ��˵�ַ */
		String sendAddress = orderForm.getSendAddress();
		/* ��ȡ�ļ��˹̶��绰 */
		String sendTel = orderForm.getSendTel();
		/* ��ȡ�ļ����ƶ��绰 */
		String sendMobile = orderForm.getSendMobile();
		/* ��ȡ�ļ����ʱ� */
		String sendPostCode = orderForm.getSendPostCode();
		/* ��ȡ�������� */
		double weight = new Double(orderForm.getWeight());
		/* ��ȡ������� */
		double volume = new Double(orderForm.getVolume());
		/* ��ȡ��Ʒ�Ĺ��� */
		String gPrice = orderForm.getGoodsPrice();
		double goodsPrice;//���۷�
		double goodsPrice2;//����
		//double goodsPrice = new Double(orderForm.getGoodsPrice());
		/* ��ȡ��ע */
		String remark = orderForm.getRemark();
		/* ��ȡ�Ƿ񱣼۵���Ϣ */
		//String pay = request.getParameter("pay");
		/* �ܷ��� */
		double totalprice = 0;
		double totalprice1 = 0;

		/* ��javabean��װ��Ϣ */
		Order order = new Order();
		/* ��װ�ռ�����Ϣ */
		order.setReceivename(receiveName);
		order.setReceiveaddress(receiveAddress);
		order.setReceivetel(receiveTel);
		order.setReceivemobile(receiveMobile);
		order.setReceivepostcode(receivePostCode);
		/* ��װ�ļ�����Ϣ */
		order.setSendname(sendName);
		order.setSendaddress(sendAddress);
		order.setSendtel(sendTel);
		order.setSendmobile(sendMobile);
		order.setSendpostcode(sendPostCode);
		/* ��װ������Ϣ */
		order.setWeight(weight);
		order.setVolume(volume);
		
		order.setRemark(remark);

		/* ���ñ��淽�� addOrder���� */
		OrderDao dao = (OrderDao) super.getBean("OrderDao");

		DeliverySpotDao Ddao = (DeliverySpotDao) super
				.getBean("DeliverySpotDao");
		/* ͨ����¼��õ�¼�����͵��ID */
		HttpSession session = request.getSession();

		User u = (User) session.getAttribute("UserSession");
		Integer DeliverySpotID = u.getDeliveryspot().getDeliveryspotid();
		Deliveryspot ds = Ddao
				.queryDeliverySpotByDeliverySpotID(DeliverySpotID);
        order.setDeliveryspot(ds);

		/* ͨ��ID�õ��۸���Ϣ */
		double startprice = ds.getStartprice();
		double secondprice = ds.getSecondprice();
		double startscope = ds.getStartscope();
		double staryvolumeprice = ds.getStartvolumeprice();
		double secondvolumeprice = ds.getSecondvolumeprice();
		double startvolumescope = ds.getStartvolumescope();
		/* ͨ�����͵���׹���ι���������۸� */
		
		
		if(!"".equals(gPrice) && gPrice != null) {
			goodsPrice2 = new Double(gPrice);
			order.setGoodsprice(goodsPrice2);
			goodsPrice = goodsPrice2 * 0.01;
		} else{
			goodsPrice = 0;
		}
		
		if (weight <= startscope) {
			totalprice1 = goodsPrice + startprice;
		} else {
			totalprice1 = goodsPrice + secondprice * weight;
		}
		
		if(volume <= startvolumescope) {
			totalprice = goodsPrice + staryvolumeprice;
		}else {
			totalprice = goodsPrice + secondvolumeprice * weight;
		}
		if(totalprice1 >= totalprice) {
			order.setTotalprice(totalprice1);
		}else {
			order.setTotalprice(totalprice);
		}
		 order.setInsureprice(goodsPrice);
		/* �������������(���͵�ID+������+ʱ��ĺ���) */
		BaseDao bdao = (BaseDao) super.getBean("BaseDao");
		String orderno = "OD" + DeliverySpotID 
				+ System.currentTimeMillis();
		request.setAttribute("orderno", orderno);
		order.setOrderno(orderno);
        
		/*��������ʱ��*/
		String createtime = bdao.getCurrentTimeFromDB();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d;
		try {
			d = df.parse(createtime);
			df1.format(d);
			order.setCreatetime(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		/*����״̬*/
		Orderstate os = (Orderstate) bdao.get(Orderstate.class, new Integer(1));
        order.setOrderstate(os);	
         
		dao.addOrder(order);
		Integer orderID = order.getOrderid();
		request.setAttribute("order", order);
		return mapping.findForward("selectOrder");

	}
/*
	public ActionForward queryAllOrder(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		OrderForm orderForm = (OrderForm) form;
		 ���ò�ѯ���ж�������queryAllOrder���� 
		OrderDao dao = (OrderDao) super.getBean("OrderDao");
		List OrderList = dao.queryAllOrder();

		request.setAttribute("OrderList", OrderList);

		return mapping.findForward("selectOrder");

	}*/

	/*ͨ������ID��ѯ���� */
	public ActionForward queryOrderByOrderID(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		OrderForm orderForm = (OrderForm) form;
		OrderDao dao = (OrderDao) super.getBean("OrderDao");
		String orderID = request.getParameter("id");
		Order order = dao.queryOrderByOrderID(Integer.parseInt(orderID));
        String orderno = request.getParameter("orderno");
        request.setAttribute("orderno", orderno);
		request.setAttribute("order", order);
		return mapping.findForward("selectOrder");

	}

	/* ͨ��������Ų�ѯ���� */
	public ActionForward queryOrderByOrderNO(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		OrderForm orderForm = (OrderForm) form;
		/* ��ǰ̨��ȡ�����ı�� */
		String orderno = request.getParameter("orderno").trim();

		OrderDao dao = (OrderDao) super.getBean("OrderDao");
		Order orderno1 = dao.queryOrderByOrderNO(orderno);
		if (orderno1 == null) {
			String Msg = "�ö����Ų�����";
			request.setAttribute("Msg", Msg);
		} else {
			//System.out.println(orderno1.getDeliveryspot().getName());
			request.setAttribute("order", orderno1);
			request.setAttribute("Flag", "1");
		}
		return mapping.findForward("SelectOrderByOrderNo");

	}

	/* ͨ������״̬��ѯ���� */
	public ActionForward queryOrderByOrderStateID(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		OrderForm orderForm = (OrderForm) form;
		String orderstate = request.getParameter("orderstate");
		OrderDao dao = (OrderDao) super.getBean("OrderDao");
		
		
		String offset = request.getParameter("pager.offset");
	     int intOffset = 0;
	     
	     if(offset != null){
	    	 intOffset = new Integer(offset).intValue();
	     }
		
		List list = dao.queryOrderByOrderStateID(Integer.parseInt(orderstate),intOffset,8);
		int count = dao.countqueryOrderByOrderStateID(Integer.parseInt(orderstate));
		request.setAttribute("count", count);
		request.setAttribute("OrderstateList", list);
		request.setAttribute("orderstate", orderstate);
		request.setAttribute("Flag", "2");
		return mapping.findForward("SelectOrderByOrderNo");

	}

	
	/* ͨ�����͵��ѯ���� */
	public ActionForward queryOrderByDeliverySpotID(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		OrderForm orderForm = (OrderForm) form;
		String deliveryspot = request.getParameter("deliveryspot");
		OrderDao dao = (OrderDao) super.getBean("OrderDao");

		String offset = request.getParameter("pager.offset");
	     int intOffset = 0;
	     
	     if(offset != null){
	    	 intOffset = new Integer(offset).intValue();
	     }
		
		
		
		List list = dao.queryOrderByDeliverySpotID(Integer
				.parseInt(deliveryspot),intOffset,8);
		int count = dao.countqueryOrderByDeliverySpotID(Integer.parseInt(deliveryspot));
		request.setAttribute("DeliveryspotList", list);
		request.setAttribute("count", count);
		request.setAttribute("deliveryspot", deliveryspot);
		request.setAttribute("Flag", "3");
		return mapping.findForward("SelectOrderByOrderNo");
	}

	/*ͨ��ʱ���ѯ����*/
	public ActionForward queryHQ(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		OrderForm orderForm = (OrderForm) form;
		String fromTime = request.getParameter("txtDate1");
		String toTime = request.getParameter("txtDate2");
		String offset = request.getParameter("pager.offset");
	     int intOffset = 0;
	     
	     if(offset != null){
	    	 intOffset = new Integer(offset).intValue();
	     }
		OrderDao rdao = (OrderDao) super.getBean("OrderDao");
		List list = rdao.queryOrderByCreateTime(fromTime, toTime, intOffset, 8);
		int count = rdao.countqueryOrderByCreateTime(fromTime, toTime);
		request.setAttribute("count", count);
		request.setAttribute("TimeList", list);
		request.setAttribute("txtDate1", fromTime);
		request.setAttribute("txtDate2", toTime);
		request.setAttribute("Flag", "4");
		return mapping.findForward("SelectOrderByOrderNo");
	}

}