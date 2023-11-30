from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains
import time
import re 
import pandas as pd

path = 'C:\Program Files (x86)\chromedriver.exe'
driver = webdriver.Chrome(path)


driver.get("https://www.caixabank.es/particular/hipotecas.html")
driver.implicitly_wait(10)

btn_cookies = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.ID,"cookies-accept-full"))).click()
time.sleep(2)

irSimulador = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//div[@class='c-2col-ul__element-wrap'])[1]"))).click()

#Nueva o segunda mano

var_tipo_vivienda = "nueva"

if var_tipo_vivienda == "nueva":
    nueva = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.CSS_SELECTOR,"label[for='nueva']"))).click()
else:
    segunda_mano = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.CSS_SELECTOR,"label[for='segundamano']"))).click()

var_uso_vivienda = "habitual"
if var_uso_vivienda == "habitual":
    habitual = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.CSS_SELECTOR,"label[for='habitual']"))).click()
else:
    segunda_vivienda = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.CSS_SELECTOR,"label[for='segundavivienda']"))).click()

selectProvincia = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//select[@id='provincia'])[1]")))

selectProvincia = Select(selectProvincia)

options = selectProvincia.options
var_provincia = "madrid"
var_provincia = var_provincia.lower()
for opt in options:
    opcion = opt.text
    opcion = opcion.lower()
    if var_provincia in opcion:
        selectProvincia.select_by_visible_text(opt.text)


edad = "18"

selectEdad = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.ID,"edad")))

selectEdad = Select(selectEdad)

options = selectEdad.options

for opt in options:
    opcion = opt.text
    if edad in opcion:
        selectEdad.select_by_visible_text(opt.text)
time.sleep(2)

precio_vivienda = 500000

input_precio = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.ID,"numInputPrecio")))
input_precio.send_keys(Keys.CONTROL + "a")
input_precio.send_keys("500000")

prestamo_max = precio_vivienda * 0.8

input_prestamo = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.ID,"numInputDinero")))
input_prestamo.send_keys(Keys.CONTROL + "a")
input_prestamo.send_keys("350000")

plazoAPagar = "25"
input_plazo = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.ID,"numInputPlazo")))
input_plazo.send_keys(Keys.CONTROL + "a")
input_plazo.send_keys("25")


btn_calcular = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.CSS_SELECTOR,"input[value='Calcular hipoteca']"))).click()

###############################################
##################OPCIONES#####################
###############################################
opciones = []
nombre_hipoteca = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.CSS_SELECTOR,"span[class='fija'] span[class='title'] span")))
nombre_hipoteca = nombre_hipoteca.text

porcentajes = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//span[@class='percent'])[1]")))
porcentajes = porcentajes.text

# Extrae el valor de la TIN utilizando regex
tin = re.search(r'TIN (\d+,\d+)', porcentajes).group(1)

# Extrae el valor de la TAE utilizando regex
tae = re.search(r'TAE (\d+,\d+)', porcentajes).group(1)

opcion = {
    "tipo" : nombre_hipoteca,
    "tin" : tin,
    "tae" : tae
    
}

opciones.append(opcion)


###############################################
################# OPCION 2 ####################
###############################################
nombre_hipoteca = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//span[contains(text(),'CasaFácil Fijo Bonificada')])[1]")))
nombre_hipoteca = nombre_hipoteca.text

porcentajes = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//span[@class='percent'])[2]")))
porcentajes = porcentajes.text

# Extrae el valor de la TIN utilizando regex
tin = re.search(r'TIN (\d+,\d+)', porcentajes).group(1)

# Extrae el valor de la TAE utilizando regex
tae = re.search(r'TAE (\d+,\d+)', porcentajes).group(1)

opcion = {
    "tipo" : nombre_hipoteca,
    "tin" : tin,
    "tae" : tae
    
}

opciones.append(opcion)

print(opciones)

time.sleep(10)