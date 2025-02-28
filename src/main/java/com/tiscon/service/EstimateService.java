package com.tiscon.service;

import com.tiscon.code.OptionalServiceType;
import com.tiscon.code.PackageType;
import com.tiscon.dao.EstimateDao;
import com.tiscon.domain.Customer;
import com.tiscon.domain.CustomerOptionService;
import com.tiscon.domain.CustomerPackage;
import com.tiscon.dto.UserOrderDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 引越し見積もり機能において業務処理を担当するクラス。
 *
 * @author Oikawa Yumi
 */
@Service
public class EstimateService {

    /** 引越しする距離の1 kmあたりの料金[円] */
    private static final int PRICE_PER_DISTANCE = 100;

    private final EstimateDao estimateDAO;

    /**
     * コンストラクタ。
     *
     * @param estimateDAO EstimateDaoクラス
     */
    public EstimateService(EstimateDao estimateDAO) {
        this.estimateDAO = estimateDAO;
    }

    /**
     * 見積もり依頼をDBに登録する。
     *
     * @param dto 見積もり依頼情報
     */
    @Transactional
    public void registerOrder(UserOrderDto dto) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);
        estimateDAO.insertCustomer(customer);

        if (dto.getWashingMachineInstallation()) {
            CustomerOptionService washingMachine = new CustomerOptionService();
            washingMachine.setCustomerId(customer.getCustomerId());
            washingMachine.setServiceId(OptionalServiceType.WASHING_MACHINE.getCode());
            estimateDAO.insertCustomersOptionService(washingMachine);
        }

        if (dto.getBoxCollect()) {
            CustomerOptionService boxCollect = new CustomerOptionService();
            boxCollect.setCustomerId(customer.getCustomerId());
            boxCollect.setServiceId(OptionalServiceType.WASHING_MACHINE.getCode());
            estimateDAO.insertCustomersOptionService(boxCollect);
        }

        if (dto.getNewLifeSet()) {
            CustomerOptionService newLifeSet = new CustomerOptionService();
            newLifeSet.setCustomerId(customer.getCustomerId());
            newLifeSet.setServiceId(OptionalServiceType.NEW_LIFE_SET.getCode());
            estimateDAO.insertCustomersOptionService(newLifeSet);
        }

        if (dto.getFurnitureSetting()) {
            CustomerOptionService furnitureSetting = new CustomerOptionService();
            furnitureSetting.setCustomerId(customer.getCustomerId());
            furnitureSetting.setServiceId(OptionalServiceType.FURNITURE_SETTING.getCode());
            estimateDAO.insertCustomersOptionService(furnitureSetting);
        }

        if (dto.getPublicFee()) {
            CustomerOptionService publicFee = new CustomerOptionService();
            publicFee.setCustomerId(customer.getCustomerId());
            publicFee.setServiceId(OptionalServiceType.FURNITURE_SETTING.getCode());
            estimateDAO.insertCustomersOptionService(publicFee);
        }

        List<CustomerPackage> packageList = new ArrayList<>();

        packageList.add(new CustomerPackage(customer.getCustomerId(), PackageType.BOX.getCode(), dto.getBox()));
        packageList.add(new CustomerPackage(customer.getCustomerId(), PackageType.BED.getCode(), dto.getBed()));
        packageList.add(new CustomerPackage(customer.getCustomerId(), PackageType.BICYCLE.getCode(), dto.getBicycle()));
        packageList.add(new CustomerPackage(customer.getCustomerId(), PackageType.WASHING_MACHINE.getCode(), dto.getWashingMachine()));
        estimateDAO.batchInsertCustomerPackage(packageList);
    }

    /**
     * 見積もり依頼に応じた概算見積もりを行う。
     *
     * @param dto 見積もり依頼情報
     * @return 概算見積もり結果の料金
     */
    public Integer getPrice(UserOrderDto dto) {
        double distance = estimateDAO.getDistance(dto.getOldPrefectureId(), dto.getNewPrefectureId());
        //季節係数(3~4月:1.5,9月:1.2,other:1)
        double N = estimateDAO.getN(dto.getMoveMonthId());
        // 小数点以下を切り捨てる
        int distanceInt = (int) Math.floor(distance);
        

        
        // 距離当たりの料金を算出する
        int priceForDistance = distanceInt * PRICE_PER_DISTANCE;

        int boxes = getBoxForPackage(dto.getBox(), PackageType.BOX)
                + getBoxForPackage(dto.getBed(), PackageType.BED)
                + getBoxForPackage(dto.getBicycle(), PackageType.BICYCLE)
                + getBoxForPackage(dto.getWashingMachine(), PackageType.WASHING_MACHINE);

        // 箱に応じてトラックの種類が変わり、それに応じて料金が変わるためトラック料金を算出する。
        int pricePerTruck = estimateDAO.getPricePerTruck(boxes);

        // オプションサービスの料金を算出する。
        int priceForOptionalService = 0;

        if (dto.getWashingMachineInstallation()) {
            priceForOptionalService += estimateDAO.getPricePerOptionalService(OptionalServiceType.WASHING_MACHINE.getCode());
        }

        if (dto.getBoxCollect()) {
            priceForOptionalService += estimateDAO.getPricePerOptionalService(OptionalServiceType.BOX_COLLECT.getCode());
        }

        if (dto.getNewLifeSet()) {
            priceForOptionalService += estimateDAO.getPricePerOptionalService(OptionalServiceType.NEW_LIFE_SET.getCode());
        }

        if (dto.getFurnitureSetting()) {
            priceForOptionalService += estimateDAO.getPricePerOptionalService(OptionalServiceType.FURNITURE_SETTING.getCode());
        }

        if (dto.getPublicFee()) {
            priceForOptionalService += estimateDAO.getPricePerOptionalService(OptionalServiceType.PUBLIC_FEE.getCode());
        }


       


        return (int) Math.floor(N * (priceForDistance + pricePerTruck) + priceForOptionalService);
    }

    /**
     * 荷物当たりの段ボール数を算出する。
     *
     * @param packageNum 荷物数
     * @param type       荷物の種類
     * @return 段ボール数
     */
    private int getBoxForPackage(int packageNum, PackageType type) {
        return packageNum * estimateDAO.getBoxPerPackage(type.getCode());
    }
}