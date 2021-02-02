package com.vpipl.drdawakhana;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.vpipl.drdawakhana.Adapters.AddCartCheckout_Adapter;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;

import ru.nikartm.support.ImageBadgeView;

public class AddCartCheckOut_Activity extends AppCompatActivity {

    public static LinearLayout layout_cartProductList;
    public static LinearLayout layout_noData;
    public static ImageBadgeView mukesh_begview;
    TextView txt_no_of_items, txt_add_more_items;
    public ListView list_cartProducts;

    public static TextView txt_subTotalAmount, txt_deliveryCharge, txt_netpayable;

    public Button btn_startShopping;

    String TAG = "AddCartCheckOut_Activity";
    AddCartCheckout_Adapter adapter;
    Activity act = AddCartCheckOut_Activity.this;

    ImageView img_menu;
    ImageView img_search;
    static ImageView img_cart;
    ImageView img_user;

    public static void setBadge() {
        mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
    }

    public ViewGroup CartListFooterView = null;

    public static String calculateSelectedProductTotalAmount() {
        double amount = 0.0d;
        try {
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                double countAmount = 0.0d;
                countAmount = ((Double.parseDouble(AppController.selectedProductsList.get(i).getNewDP())) * (Double.parseDouble(AppController.selectedProductsList.get(i).getQty())));
                amount = amount + countAmount;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ((int) amount) + "";
    }


    public static String calculateSelectedProductTotalShipCharge() {

        double amount = 0d;
        return ((int) amount) + "";
    }

    public static String calculateSelectedProductTotalNetPayable() {
        double amount = 0.0d;
        try {
            amount = (Double.parseDouble(calculateSelectedProductTotalAmount()) + Double.parseDouble(calculateSelectedProductTotalShipCharge()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ((int) amount) + "";
    }

    public static void setNetPaybleValue() {
        try {
            btn_ttl_amount.setText("₹ " + Html.fromHtml(calculateSelectedProductTotalNetPayable()));

            txt_subTotalAmount.setText("\u20B9 " + Html.fromHtml(calculateSelectedProductTotalAmount()));
            txt_deliveryCharge.setText("₹ " + Html.fromHtml(calculateSelectedProductTotalShipCharge()));
            txt_netpayable.setText("₹ " + Html.fromHtml(calculateSelectedProductTotalNetPayable()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showEmptyCart() {
        try {
            layout_cartProductList.setVisibility(View.GONE);
            layout_noData.setVisibility(View.VISIBLE);

            if (AppController.selectedProductsList.size() > 0) {
                if (img_cart != null)
                    img_cart.setVisibility(View.GONE);
            } else {
                if (img_cart != null)
                    img_cart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   static Button btn_checkout ,btn_ttl_amount ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcartcheckout_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            layout_cartProductList = (LinearLayout) findViewById(R.id.layout_cartProductList);
            layout_noData = (LinearLayout) findViewById(R.id.layout_noData);
            list_cartProducts = (ListView) findViewById(R.id.list_cartProducts);

            txt_no_of_items = (TextView) findViewById(R.id.txt_no_of_items);
            txt_add_more_items = (TextView) findViewById(R.id.txt_add_more_items);
            btn_startShopping = (Button) findViewById(R.id.btn_startShopping);

            btn_checkout  = (Button) findViewById(R.id.btn_checkout);
            btn_ttl_amount = (Button) findViewById(R.id.btn_ttl_amount);

            if (AppController.selectedProductsList.size() == 1) {
                txt_no_of_items.setText(AppController.selectedProductsList.size() + " Item in the cart");
            } else {
                txt_no_of_items.setText(AppController.selectedProductsList.size() + " Items in the cart");
            }
            txt_add_more_items.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, MainActivity.class));
                    finish();
                }
            });

            btn_startShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    Intent intent = new Intent(act, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            mukesh_begview = findViewById(R.id.mukesh_begview);
            mukesh_begview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, com.vpipl.drdawakhana.AddCartCheckOut_Activity.class));
                    finish();
                }
            });
            mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());

            btn_ttl_amount.setText("₹ " + Html.fromHtml(calculateSelectedProductTotalNetPayable()));
            btn_checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                        startActivity(new Intent(act, CheckoutToPay_Activity.class));
                    } else {
                        showLoginRegisterDialog();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoginRegisterDialog() {
        try {

            final Dialog dialog = new Dialog(this, R.style.ThemeDialogCustom);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.custom_dialog_three);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Please Select an option to continue."));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Register");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        startActivity(new Intent(act, Register_User_Activity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText("Login");
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        startActivity(new Intent(act, Login_Activity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProductSelectedCartList() {
        try {
            if (AppController.selectedProductsList.size() > 0) {

                layout_cartProductList.setVisibility(View.VISIBLE);
                layout_noData.setVisibility(View.GONE);

                if (list_cartProducts.getFooterViewsCount() > 0) {
                    View v = list_cartProducts.findViewWithTag("footer");
                    if (v != null) {
                        list_cartProducts.removeFooterView(v);
                    }
                }

                /*LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                CartListFooterView = (ViewGroup) inflater.inflate(R.layout.cartlist_footer, list_cartProducts, false);
                CartListFooterView.setTag("footer");

                setFooterDetails();

                list_cartProducts.addFooterView(CartListFooterView, null, false);*/

                adapter = new AddCartCheckout_Adapter(act);
                list_cartProducts.setAdapter(adapter);
                adapter.notifyDataSetChanged();


                list_cartProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    }
                });

                if (AppController.selectedProductsList.size() > 0) {
                    if (img_cart != null)
                        img_cart.setVisibility(View.GONE);
                } else {
                    if (img_cart != null)
                        img_cart.setVisibility(View.GONE);
                }

                setNetPaybleValue();

                showCartData();
            } else {
                showEmptyCart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFooterDetails() {
        try {
            if (CartListFooterView != null) {

                txt_subTotalAmount = (TextView) CartListFooterView.findViewById(R.id.txt_subTotalAmount);

                txt_deliveryCharge = (TextView) CartListFooterView.findViewById(R.id.txt_deliveryCharge);
                txt_netpayable = (TextView) CartListFooterView.findViewById(R.id.txt_netpayable);

                btn_checkout = (Button) CartListFooterView.findViewById(R.id.btn_checkout);

                txt_subTotalAmount.setText("\u20B9 " + Html.fromHtml(calculateSelectedProductTotalAmount()));
                txt_deliveryCharge.setText("₹ " + Html.fromHtml(calculateSelectedProductTotalShipCharge()));
                txt_netpayable.setText("₹ " + Html.fromHtml(calculateSelectedProductTotalNetPayable()));

                btn_checkout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                            startActivity(new Intent(act, CheckoutToPay_Activity.class));
                        } else {
                            showLoginRegisterDialog();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCartData() {
        try {
            layout_cartProductList.setVisibility(View.VISIBLE);
            layout_noData.setVisibility(View.GONE);

            if (AppController.selectedProductsList.size() > 0) {
                if (img_cart != null)
                    img_cart.setVisibility(View.GONE);
            } else {
                if (img_cart != null)
                    img_cart.setVisibility(View.GONE);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            if (AppController.selectedProductsList.size() > 0) {
                setProductSelectedCartList();
            } else {
                showEmptyCart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (AppController.selectedProductsList.size() > 0) {
                setProductSelectedCartList();
            } else {
                showEmptyCart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cartClearAll() {
        try {
            AppController.selectedProductsList.clear();
            showEmptyCart();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {

            AppUtils.dismissProgressDialog();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    public void SetupToolbar() {
        LinearLayout img_nav_back = findViewById(R.id.img_nav_back);
        TextView heading = findViewById(R.id.heading);
        heading.setText("Your Cart");
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

      /*  img_search = (ImageView) findViewById(R.id.img_search);
        img_cart = (ImageView) findViewById(R.id.img_cart);
        img_user = (ImageView) findViewById(R.id.img_user);
        img_search.setVisibility(View.GONE);

        img_menu.setImageResource(R.drawable.ic_arrow_back_white_px);

        img_cart.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_cart_empty));



        img_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartClearAll();
            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(act, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(act);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_log_out));
        else
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_user_login));
*/

    }
}