#!/bin/sh
# AWS Vormetric DSM  instance create script 
start_time="$(date -u +%s)"

#find our vpc
vpcID=$(aws ec2 describe-vpcs --query 'Vpcs[*][VpcId]' --output text |  sort -r | head -1)
echo "vpcid=$vpcID"
#could have mutliple dsm ami's. find most recent one.
dsmAMI=$(aws ec2 describe-images --owners self --query 'Images[*][CreationDate , Name,ImageId]' --output text | grep dsm | sort -r | head -1 | awk '{print $3}')
echo "dsmami=$dsmAMI"
#Create the DSM security groups
dsmSG=$(aws ec2 create-security-group --group-name DSM_Security_Group-sg --description "DSM_Security_Group" --vpc-id $vpcID --output text)
echo "dsmSG=$dsmSG"

#ip=$(curl https://checkip.amazonaws.com/)
#cidr="$ip/24"
cidr="0.0.0.0/0"
echo "using cidr of $cidr"
echo "Setting DSM inbound rules"
aws ec2 authorize-security-group-ingress --group-id $dsmSG --protocol tcp --port 22 --cidr $cidr
aws ec2 authorize-security-group-ingress --group-id $dsmSG --protocol tcp --port 443 --cidr $cidr
aws ec2 authorize-security-group-ingress --group-id $dsmSG --protocol tcp --port 8445 --cidr $cidr
aws ec2 authorize-security-group-ingress --group-id $dsmSG --protocol tcp --port 8080 --cidr $cidr
aws ec2 authorize-security-group-ingress --group-id $dsmSG --protocol tcp --port 8443-8444 --cidr $cidr
aws ec2 authorize-security-group-ingress --group-id $dsmSG --protocol tcp --port 8446-8448 --cidr $cidr
aws ec2 authorize-security-group-ingress --group-id $dsmSG --protocol tcp --port 50000 --cidr $cidr
aws ec2 authorize-security-group-ingress --group-id $dsmSG --protocol tcp --port 50501-50508 --cidr $cidr
aws ec2 authorize-security-group-ingress --group-id $dsmSG --protocol tcp --port 7025 --cidr $cidr

vteSG=$(aws ec2 create-security-group --group-name VTE_Agent_Security_Group-sg --description "VTE_Agent_Security_Group" --vpc-id $vpcID --output text)
echo "vtesg=$vteSG"
echo "Setting VTE Agent inbound rules"
aws ec2 authorize-security-group-ingress --group-id $vteSG --protocol tcp --port 7024 --cidr $cidr
aws ec2 authorize-security-group-ingress --group-id $vteSG --protocol tcp --port 22 --cidr $cidr

echo "Creating DSM Instance "
#aws ec2 run-instances --image-id $dsmAMI --count 1 --instance-type m4.large --security-group-ids $dsmSG 
#Create DSM instance.  Need the subnet so Agent can be in same network.
dsmInstanceId=$(aws ec2 run-instances --image-id $dsmAMI --count 1 --instance-type m4.large --security-group-ids $dsmSG | grep InstanceId | awk '{print $2}' | tr -d '"' | tr -d ',')
if [ $? -ne 0 ]; then
   echo "Failed to Create DSM instance"
   exit 1
fi
echo "dsmInstanceId=$dsmInstanceId"

dsmSubnet=$(aws ec2 describe-instances --instance-ids $dsmInstanceId --query 'Reservations[0].Instances[0].SubnetId' | tr -d '"')
echo "dsmSubnet=$dsmSubnet"

echo "Creating VTE Ubuntu instance "
aws ec2 run-instances --image-id ami-059eeca93cf09eebd --count 1 --instance-type t2.micro --key-name MyKeyPair --security-group-ids $vteSG --subnet-id $dsmSubnet
if [ $? -ne 0 ]; then
   echo "Failed to Create VTE Agent instance"
   exit 1
fi
end_time="$(date -u +%s)"
elapsed="$(($end_time-$start_time))"
echo "Total of $elapsed seconds elapsed for process"
echo "Done "
exit 0
