CREATE TABLE `product` (
  `id` int(11) NOT NULL,
  `tenant_id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `zzcmn_fdate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`, `tenant_id`)
);

INSERT INTO `product` (`id`, `tenant_id`, `name`, `quantity`, `zzcmn_fdate`) VALUES (1, 'default', 'test', 10, NOW());