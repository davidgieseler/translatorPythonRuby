def verificar_elemento(x):
    if x > 2:
        print("O primeiro elemento é maior que dois.")
    elif x == 2:
        print("O primeiro elemento é dois.")
    else:
        print("O primeiro elemento é menor que dois.")

def imprimir_ate_quatro(x):
    while x < 4:
        print(x)
        x += 1

def main():
    x = 2
    verificar_elemento(x)
    imprimir_ate_quatro(x)

if __name__ == "__main__":
    main()

numbers = [1, 2, 3, 4, 5]
for number in numbers:
    print(number)